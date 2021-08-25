package com.exactpro.th2.fix.client;

import com.exactpro.th2.common.event.Event;
import com.exactpro.th2.common.grpc.AnyMessage;
import com.exactpro.th2.common.grpc.ConnectionID;
import com.exactpro.th2.common.grpc.EventBatch;
import com.exactpro.th2.common.grpc.MessageGroupBatch;
import com.exactpro.th2.common.schema.factory.CommonFactory;
import com.exactpro.th2.common.schema.grpc.router.GrpcRouter;
import com.exactpro.th2.common.schema.message.MessageListener;
import com.exactpro.th2.common.schema.message.MessageRouter;
import com.exactpro.th2.common.schema.message.MessageRouterUtils;
import com.exactpro.th2.common.schema.message.SubscriberMonitor;
import com.exactpro.th2.fix.client.fixBean.FixBean;
import com.exactpro.th2.fix.client.impl.Destructor;
import com.exactpro.th2.fix.client.service.FixBeanService;
import com.exactpro.th2.fix.client.util.MessageUtil;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    private static final String INPUT_QUEUE_ATTRIBUTE = "send";
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static class Resources {
        String name;
        Destructor destructor;

        public String getName() {
            return name;
        }

        public Destructor getDestructor() {
            return destructor;
        }

        public Resources(String name, Destructor destructor) {
            this.name = name;
            this.destructor = destructor;


        }
    }

    public static void main(String[] args) throws ConfigError {
        ConcurrentLinkedDeque<Resources> resources = new ConcurrentLinkedDeque<>();
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(
                    () -> resources.descendingIterator().forEachRemaining(resource -> {
                        LOGGER.debug("Destroying resource: " + resource.name);
                        try {
                            resource.destructor.close();
                        } catch (Exception e) {
                            LOGGER.error("Failed to destroy resource: " + resource.name);
                        }
                    })
            ));
        } catch (RuntimeException e) {
            LOGGER.error("Uncaught exception. Shutting down");
            System.exit(1);
            throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.", e);
        }

        CommonFactory factory;
        try {
            factory = CommonFactory.createFromArguments(args);
            resources.add(new Resources("factory", factory::close));
        } catch (Exception e) {
            factory = new CommonFactory();
            LOGGER.error("Failed to create common factory from args", e);
        }

        JsonMapper mapper = JsonMapper.builder().build();

        Settings settings = factory.getCustomConfiguration(Settings.class, mapper);
        MessageRouter<EventBatch> eventRouter = factory.getEventBatchRouter();
        MessageRouter<MessageGroupBatch> messageRouter = factory.getMessageRouterMessageGroupBatch();
        GrpcRouter grpcRouter = factory.getGrpcRouter();

        run(settings, messageRouter, eventRouter, grpcRouter, resources);

    }

    public static void run(Settings settings, MessageRouter<MessageGroupBatch> messageRouter, MessageRouter<EventBatch> eventRouter,
                           GrpcRouter grpcRouter, Deque<Resources> resources) throws ConfigError {

        FixBeanService fixBeanService = new FixBeanService();
        File configFile = fixBeanService.createConfig(settings.fixBeanList);

        resources.add(new Resources("config file", configFile::deleteOnExit));

        List<String> sessionAliases = fixBeanService.getSessionAliases(settings.fixBeanList);
        List<ConnectionID> connectionIDS = new ArrayList<>();

        for (String sessionAlias : sessionAliases) {
            connectionIDS.add(ConnectionID.newBuilder().setSessionAlias(sessionAlias).build());
        }

        String joinedSessionAliases = String.join(":", sessionAliases);

        Event curEvent = MessageRouterUtils.storeEvent(eventRouter, Event.start(), null);
        curEvent.name("FIX client " + joinedSessionAliases + Instant.now());
        curEvent.type("Microservice");
        String rootEventId = curEvent.getId();

        FixClient fixClient = new FixClient(new SessionSettings(configFile.getAbsolutePath()),
                messageRouter, eventRouter, connectionIDS, rootEventId);

        resources.add(new Resources("client", fixClient::stop));

        ClientController controller = new ClientController(fixClient);

        MessageListener<MessageGroupBatch> listener = (consumerTag, groupBatch) -> {
            if (!controller.isRunning) controller.start(settings.autoStopAfter);

            groupBatch.getGroupsList().forEach((group) -> {
                try {
                    if (group.getMessagesCount() != 1) {
                        LOGGER.error("Message group contains more than 1 message", new IllegalArgumentException());
                        throw new IllegalArgumentException();
                    }
                    AnyMessage message = group.getMessagesList().get(0);
                    if (!message.hasRawMessage()) {
                        LOGGER.error("Message in the group is not a raw message", new IllegalArgumentException());
                        throw new IllegalArgumentException();
                    }

                    SessionID sessionID = MessageUtil.getSessionID(fromAnyMessageToString(message));
                    Session session = Session.lookupSession(sessionID);
                    Message fixMessage = MessageUtils.parse(session, fromAnyMessageToString(message));
                    session.send(fixMessage);

                } catch (Exception e) {
                    try {
                        LOGGER.error("Failed to handle message group: " + MessageUtil.toPrettyString(group), e); //todo toPrettyString do not work
                        MessageRouterUtils.storeEvent(eventRouter, rootEventId, "Failed to handle message group: " + MessageUtil.toPrettyString(group), "Error", e);
                    } catch (InvalidProtocolBufferException invalidProtocolBufferException) {
                        LOGGER.error("Failed to handle message group: ", invalidProtocolBufferException);
                    }
                }
            });
        };

        try {
            SubscriberMonitor monitor = messageRouter.subscribe(listener, INPUT_QUEUE_ATTRIBUTE);
            if (monitor == null) throw new NullPointerException();
            resources.add(new Resources("raw-monitor", monitor::unsubscribe));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to subscribe to input queue");
        }

        if (settings.autoStart) fixClient.start();
        if (settings.grpcStartControl) grpcRouter.startServer(new ControlService(controller));

        LOGGER.info("Successfully started");

        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        resources.add(new Resources("await-shutdown", () -> {
            lock.lock();
            condition.signalAll();
            lock.unlock();
        }
        ));

        try {
            lock.lock();
            condition.await();
            lock.unlock();
        } catch (InterruptedException e) {
            LOGGER.error("Cannot get lock for session", e);
        }

        LOGGER.info("Finished running");

    }
    private static String fromAnyMessageToString(AnyMessage message){
        return new String(message.getRawMessage().getBody().toByteArray(), StandardCharsets.UTF_8);
    }

    public static class Settings extends FixBean {
        boolean grpcStartControl = false;
        boolean autoStart = true;
        int autoStopAfter = 0;
        List<FixBean> fixBeanList;

        public void setFixBeanList(List<FixBean> fixBeanList) {
            this.fixBeanList = fixBeanList;
        }

        public boolean isGrpcStartControl() {
            return grpcStartControl;
        }

        public void setGrpcStartControl(boolean grpcStartControl) {
            this.grpcStartControl = grpcStartControl;
        }

        public boolean isAutoStart() {
            return autoStart;
        }

        public void setAutoStart(boolean autoStart) {
            this.autoStart = autoStart;
        }

        public int getAutoStopAfter() {
            return autoStopAfter;
        }

        public void setAutoStopAfter(int autoStopAfter) {
            this.autoStopAfter = autoStopAfter;
        }

        public List<FixBean> getFixBeanList() {
            return fixBeanList;
        }
    }

}
