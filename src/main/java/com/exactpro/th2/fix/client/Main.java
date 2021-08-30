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
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.ConfigError;
import quickfix.DataDictionary;
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
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {

    private static final String INPUT_QUEUE_ATTRIBUTE = "send";
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static class Resources {
        private final String name;
        private final Destructor destructor;

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

    public static void main(String[] args) {
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
                if (!zipEntry.getName().matches("FIX\\.[4-5]\\.[0-4]\\.xml"))
                    throw new IllegalArgumentException("Incorrect file name for FIX dictionary"); //should we create a custom exception for this case?
                File dataDict = File.createTempFile(zipEntry.getName(), "xml");
                Files.write(dataDict.toPath(), zin.readAllBytes());
                new DataDictionary(dataDict.getAbsolutePath()); //check that xml file contains the correct values
                dictionaries.add(dataDict);
                dataDict.deleteOnExit();
            }
        } catch (IOException | NullPointerException | ConfigError e) {
            LOGGER.error("Failed to create DataDictionary", e);
            System.exit(1);
        }

        for (FixBean fixBean : settings.sessionsSettings) {
            for (File dataDict : dictionaries) {
                if (dataDict.getName().equals(fixBean.getBeginString() + ".xml"))
                    fixBean.setDataDictionary(dataDict.getAbsolutePath());
            }
            if (fixBean.getDataDictionary() == null || fixBean.getDataDictionary().equals("")) {
                throw new NullPointerException("Data Dictionary does not set");
            }
        }

        MessageRouter<EventBatch> eventRouter = factory.getEventBatchRouter();
        MessageRouter<MessageGroupBatch> messageRouter = factory.getMessageRouterMessageGroupBatch();
        GrpcRouter grpcRouter = factory.getGrpcRouter();

        try {
            run(settings, messageRouter, eventRouter, grpcRouter, resources);
        } catch (IOException e) {
            LOGGER.error("Failed to create config file", e);
            System.exit(1);
        } catch (ConfigError e) {
            LOGGER.error("Failed to load file with session settings", e);
            System.exit(1);
        }

    }

    public static void run(Settings settings, MessageRouter<MessageGroupBatch> messageRouter, MessageRouter<EventBatch> eventRouter,
                           GrpcRouter grpcRouter, Deque<Resources> resources) throws IOException, ConfigError {

        File configFile = FixBeanUtil.createConfig(settings);

        StringBuilder sb = new StringBuilder();
        Map<SessionID, ConnectionID> connections = new HashMap<>();

        for (int i = 0; i < settings.sessionsSettings.size(); i++) {
            FixBean fixBean = settings.sessionsSettings.get(i);
            SessionID sessionID = new SessionID(fixBean.getBeginString(), fixBean.getSenderCompID(),
                    fixBean.getSenderSubID(), fixBean.getSenderLocationID(), fixBean.getTargetCompID(),
                    fixBean.getTargetSubID(), fixBean.getTargetLocationID(), fixBean.getSessionQualifier());

            String sessionAlias = fixBean.getSessionAlias();
            connections.put(sessionID, ConnectionID.newBuilder().setSessionAlias(sessionAlias).build());
            sb.append(sessionAlias);
            if (i < settings.sessionsSettings.size() - 1) sb.append(":");
        }

        Event curEvent = MessageRouterUtils.storeEvent(eventRouter, Event.start(), null);
        curEvent.name("FIX client " + sb + Instant.now());
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
                        if (LOGGER.isErrorEnabled()) {
                            LOGGER.error("Message group contains more than 1 message {} ", com.exactpro.th2.common.message.MessageUtils.toJson(group));
                        }
                    }
                    AnyMessage message = group.getMessagesList().get(0);
                    if (!message.hasRawMessage()) {
                        if (LOGGER.isErrorEnabled()) {
                            LOGGER.error("Message in the group is not a raw message {} ", com.exactpro.th2.common.message.MessageUtils.toJson(message));
                        }
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
            SubscriberMonitor monitor = Objects.requireNonNull(messageRouter.subscribe(listener, INPUT_QUEUE_ATTRIBUTE), "Subscriber monitor must not be null.");
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

    public static class Settings extends FixBean {
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
            if (autoStopAfter < 0)
                throw new IllegalArgumentException("Timer for automatically stopping the client cannot be negative");
            this.autoStopAfter = autoStopAfter;
        }

        public List<FixBean> getSessionsSettings() {
            return sessionsSettings;
        }

        @Override
        public String toString() {

            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                    .append("grpcStartControl", grpcStartControl)
                    .append("autoStart", autoStart)
                    .append("autoStopAfter", autoStopAfter)
                    .append("sessionsSettings", sessionsSettings)
                    .toString();
        }
    }


}
