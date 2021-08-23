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
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import fixBean.SessionFixBean;
import impl.Destructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import util.MessageUtil;

import javax.management.JMException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    private static final String INPUT_QUEUE_ATTRIBUTE = "send";
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static class Resources {
        String name;
        Destructor destructor;

        public Resources(String name, Destructor destructor) {
            this.name = name;
            this.destructor = destructor;
        }
    }

    public static void main(String[] args) throws ConfigError, FieldConvertError, JMException {
        CommonFactory factory;
        ConcurrentLinkedDeque<Resources> resources = new ConcurrentLinkedDeque<>();
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(
                    () -> resources.descendingIterator().forEachRemaining((resource) -> {
                        log.debug("Destroying resource: " + resource.name);
                        try {
                            resource.destructor.close();
                        } catch (Exception e) {
                            log.error("Failed to destroy resource: " + resource.name);
                        }
                    })
            ));
        } catch (Exception e) {
            log.error("Uncaught exception. Shutting down");
            System.exit(1);
            throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.");
        }

        try {
            factory = CommonFactory.createFromArguments(args);
            resources.add(new Resources("factory", factory::close));
        } catch (Exception e) {
            factory = new CommonFactory();
            log.error("Failed to create common factory from args", e);
        }

        JsonMapper mapper = JsonMapper.builder().build();

        Settings settings = factory.getCustomConfiguration(Settings.class, mapper);
        MessageRouter<EventBatch> eventRouter = factory.getEventBatchRouter();
        MessageRouter<MessageGroupBatch> messageRouter = factory.getMessageRouterMessageGroupBatch();
        GrpcRouter grpcRouter = factory.getGrpcRouter();

        run(settings, messageRouter, eventRouter, grpcRouter, resources);

    }

    public static void run(Settings settings, MessageRouter<MessageGroupBatch> messageRouter, MessageRouter<EventBatch> eventRouter,
                           GrpcRouter grpcRouter, ConcurrentLinkedDeque<Resources> resources) throws ConfigError, FieldConvertError, JMException {

        SessionFixBean sessionFixBean = new SessionFixBean();
        sessionFixBean.createConfig();    //create config file for FIX
        String sessionAlias = new SessionFixBean().getSessionAlias();

        ConnectionID connectionID = ConnectionID.newBuilder().setSessionAlias(sessionAlias).build();

        Event curEvent = MessageRouterUtils.storeEvent(eventRouter, Event.start(), null);
        curEvent.name("FIX client " + sessionAlias + Instant.now());
        curEvent.type("Microservice");
        String rootEventId = curEvent.getId();

        FixClient fixClient = new FixClient(new SessionSettings("src/main/resources/acceptor/acceptor.cfg"),
                messageRouter, eventRouter, connectionID, rootEventId);

        resources.add(new Resources("client", fixClient::stop));

        ClientController controller = new ClientController(fixClient);

        MessageListener<MessageGroupBatch> listener = (consumerTag, groupBatch) -> {
            if (!controller.isRunning) controller.start(settings.autoStopAfter);

            groupBatch.getGroupsList().forEach((group) -> {
                try {
                    if (group.getMessagesCount() != 1) {
                        log.error("Message group contains more than 1 message", new IllegalArgumentException());
                        throw new IllegalArgumentException();
                    }
                    AnyMessage message = group.getMessagesList().get(0);
                    if (!message.hasRawMessage()) {
                        log.error("Message in the group is not a raw message", new IllegalArgumentException());
                        throw new IllegalArgumentException();
                    }

                    Session session = fixClient.getApplication().getSession();
                    Message fixMessage = MessageUtils.parse(session, new String(message.getRawMessage().getBody().toByteArray(), StandardCharsets.UTF_8));
                    session.send(fixMessage);

                } catch (Exception e) {
                    try {
                        log.error("Failed to handle message group: " + MessageUtil.toPrettyString(group), e);
                        MessageRouterUtils.storeEvent(eventRouter, rootEventId, "Failed to handle message group: " + MessageUtil.toPrettyString(group), "Error", e);
                    } catch (InvalidProtocolBufferException invalidProtocolBufferException) {
                        log.error("Failed to handle message group: ", invalidProtocolBufferException);
                    }
                }
            });
        };

        try {
            SubscriberMonitor monitor = messageRouter.subscribe(listener, INPUT_QUEUE_ATTRIBUTE);
            if (monitor != null) {
                resources.add(new Resources("raw-monitor", monitor::unsubscribe));
            }
        } catch (Exception e) {
            log.error("Failed to subscribe to input queue", e);
        }

        if (settings.autoStart) fixClient.start();
        if (settings.grpcStartControl) grpcRouter.startServer(new ControlService(controller));

        log.info("Successfully started");

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
            log.error("Interrupted Exception in main", e);
        }

        log.info("Finished running");

    }

    public static class Settings {
        boolean grpcStartControl = false;
        boolean autoStart = true;
        int autoStopAfter = 0;
    }

}
