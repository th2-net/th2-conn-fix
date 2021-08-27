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
import com.exactpro.th2.fix.client.util.FixBeanUtil;
import com.exactpro.th2.fix.client.util.MessageUtil;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.ConfigError;
import quickfix.Message;
import quickfix.MessageUtils;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
                            LOGGER.error("Failed to destroy resource: {}", resource.name);
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

        List<File> dictionaries = new ArrayList<>();

        try (ZipInputStream zin = new ZipInputStream(factory.readDictionary())) {
            ZipEntry zipEntry;
            while ((zipEntry = zin.getNextEntry()) != null) {
                File dataDict = File.createTempFile(zipEntry.getName(), "xml");
                Files.write(dataDict.toPath(), zin.readAllBytes());
                dictionaries.add(dataDict);
                dataDict.deleteOnExit();
            }
        } catch (IOException | NullPointerException e) {
            LOGGER.error("Failed to create DataDictionary", e);
        }

        for (FixBean fixBean : settings.sessionsSettings) {
            switch (fixBean.getBeginString()) {
                case "FIX.4.0":
                    for (File dataDict : dictionaries) {
                        if (dataDict.getName().equals("FIX40.xml"))
                            fixBean.setDataDictionary(dataDict.getAbsolutePath());
                    }
                    break;
                case "FIX.4.1":
                    for (File dataDict : dictionaries) {
                        if (dataDict.getName().equals("FIX41.xml"))
                            fixBean.setDataDictionary(dataDict.getAbsolutePath());
                    }
                    break;
                case "FIX.4.2":
                    for (File dataDict : dictionaries) {
                        if (dataDict.getName().equals("FIX42.xml"))
                            fixBean.setDataDictionary(dataDict.getAbsolutePath());
                    }
                    break;
                case "FIX.4.3":
                    for (File dataDict : dictionaries) {
                        if (dataDict.getName().equals("FIX43.xml"))
                            fixBean.setDataDictionary(dataDict.getAbsolutePath());
                    }
                    break;
                case "FIX.4.4":
                    for (File dataDict : dictionaries) {
                        if (dataDict.getName().equals("FIX44.xml"))
                            fixBean.setDataDictionary(dataDict.getAbsolutePath());
                    }
                    break;
                case "FIX.5.0":
                    for (File dataDict : dictionaries) {
                        if (dataDict.getName().equals("FIX50.xml"))
                            fixBean.setDataDictionary(dataDict.getAbsolutePath());
                    }
                    break;
            }
        }

        MessageRouter<EventBatch> eventRouter = factory.getEventBatchRouter();
        MessageRouter<MessageGroupBatch> messageRouter = factory.getMessageRouterMessageGroupBatch();
        GrpcRouter grpcRouter = factory.getGrpcRouter();

        run(settings, messageRouter, eventRouter, grpcRouter, resources);

    }

    public static void run(Settings settings, MessageRouter<MessageGroupBatch> messageRouter, MessageRouter<EventBatch> eventRouter,
                           GrpcRouter grpcRouter, Deque<Resources> resources) throws ConfigError {

        File configFile = FixBeanUtil.createConfig(settings.sessionsSettings);

        StringBuilder sb = new StringBuilder();
        Map<SessionID, ConnectionID> connections = new HashMap<>();

        for (int i = 0; i < settings.sessionsSettings.size(); i++) {
            FixBean fixBean = settings.sessionsSettings.get(i);
            SessionID sessionID = new SessionID(fixBean.getBeginString(), fixBean.getSenderCompID(),
                    fixBean.getSenderSubID(), fixBean.getSenderLocationID(), fixBean.getTargetCompID(),
                    fixBean.getTargetSubID(), fixBean.getTargetLocationID(), fixBean.getSessionQualifier());

            String sessionAlias = settings.sessionsSettings.get(i).getSessionAlias();
            connections.put(sessionID, ConnectionID.newBuilder().setSessionAlias(sessionAlias).build());
            sb.append(sessionAlias);
            if (i < settings.sessionsSettings.size() - 1) sb.append(":");
        }

        Event curEvent = MessageRouterUtils.storeEvent(eventRouter, Event.start(), null);
        curEvent.name("FIX client " + sb.toString() + Instant.now());
        curEvent.type("Microservice");
        String rootEventId = curEvent.getId();

        FixClient fixClient = new FixClient(new SessionSettings(configFile.getAbsolutePath()),
                messageRouter, eventRouter, connections, rootEventId);

        configFile.deleteOnExit();
        resources.add(new Resources("client", fixClient::stop));

        ClientController controller = new ClientController(fixClient);

        MessageListener<MessageGroupBatch> listener = (consumerTag, groupBatch) -> {
            if (!controller.isRunning) controller.start(settings.autoStopAfter);

            groupBatch.getGroupsList().forEach((group) -> {
                try {
                    if (group.getMessagesCount() != 1) {
                        LOGGER.error("Message group contains more than 1 message", new IllegalArgumentException("Message group contains more than 1 message"));
                    }
                    AnyMessage message = group.getMessagesList().get(0);
                    if (!message.hasRawMessage()) {
                        LOGGER.error("Message in the group is not a raw message", new IllegalArgumentException("Message in the group is not a raw message"));
                    }

                    String strMessage = MessageUtil.rawToString(message);
                    SessionID sessionID = MessageUtil.getSessionID(strMessage);
                    Session session = Session.lookupSession(sessionID);
                    Message fixMessage = MessageUtils.parse(session, strMessage);
                    session.send(fixMessage);

                } catch (Exception e) {
                    LOGGER.error("Failed to handle message group: {}", com.exactpro.th2.common.message.MessageUtils.toJson(group), e);
                    MessageRouterUtils.storeEvent(eventRouter, rootEventId, "Failed to handle message group: " + com.exactpro.th2.common.message.MessageUtils.toJson(group), "Error", e);
                }
            });
        };

        try {
            SubscriberMonitor monitor = messageRouter.subscribe(listener, INPUT_QUEUE_ATTRIBUTE);
            if (monitor == null) throw new NullPointerException();
            resources.add(new Resources("raw-monitor", monitor::unsubscribe));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to subscribe to input queue", e);
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
            LOGGER.error("Cannot get lock for Fix Client", e);
        }

        LOGGER.info("Finished running");

    }

    public static class Settings extends FixBean { //for what we extends on FixBean?
        boolean grpcStartControl = false;
        boolean autoStart = true;
        int autoStopAfter = 0;
        List<FixBean> sessionsSettings = new ArrayList<>();

        public void setSessionsSettings(List<FixBean> sessionsSettings) {
            this.sessionsSettings = sessionsSettings;
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
            if (autoStopAfter >= 0) this.autoStopAfter = autoStopAfter;
        }

        public List<FixBean> getSessionsSettings() {
            return sessionsSettings;
        }

        @Override
        public String toString() {
            return "Settings{" +
                    "grpcStartControl=" + grpcStartControl +
                    ", autoStart=" + autoStart +
                    ", autoStopAfter=" + autoStopAfter +
                    ", sessionsSettings=" + sessionsSettings +
                    '}';
        }
    }


}
