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
import com.exactpro.th2.fix.client.exceptions.CreatingConfigFileException;
import com.exactpro.th2.fix.client.exceptions.EmptyDataDictionaryException;
import com.exactpro.th2.fix.client.exceptions.IncorrectFixFileNameException;
import com.exactpro.th2.fix.client.fixBean.FixBean;
import com.exactpro.th2.fix.client.impl.Destructor;
import com.exactpro.th2.fix.client.util.FixBeanUtil;
import com.exactpro.th2.fix.client.util.MessageUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.IncorrectDataFormat;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.exactpro.th2.common.message.MessageUtils.toJson;

public class Main {

    private static final String INPUT_QUEUE_ATTRIBUTE = "send";
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final Pattern FIX_PATTERN = Pattern.compile("FIX\\.[4-5]\\.[0-4]\\.xml");

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

    public static void main(String[] args) throws Exception {
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
                if (!zipEntry.getName().matches(FIX_PATTERN.pattern()))
                    throw new IncorrectFixFileNameException("Incorrect file name for FIX dictionary");
                File dataDict = File.createTempFile(zipEntry.getName(), "");
                Files.write(dataDict.toPath(), zin.readAllBytes());
                new DataDictionary(dataDict.getAbsolutePath()); //check that xml file contains the correct values
                dictionaries.add(dataDict);
                dataDict.deleteOnExit();
            }
        } catch (IOException | NullPointerException | ConfigError e) {
            throw new Exception("Failed to create DataDictionary", e);
        }

        for (FixBean fixBean : settings.sessionsSettings) {
            for (File dataDict : dictionaries) {
                if (dataDict.getName().equals(fixBean.getBeginString() + ".xml")) {
                    fixBean.setDataDictionary(dataDict.getAbsolutePath());
                }
            }
            if (fixBean.getDataDictionary() == null) {
                throw new EmptyDataDictionaryException("Data Dictionary does not set");
            }
        }

        MessageRouter<EventBatch> eventRouter = factory.getEventBatchRouter();
        MessageRouter<MessageGroupBatch> messageRouter = factory.getMessageRouterMessageGroupBatch();
        GrpcRouter grpcRouter = factory.getGrpcRouter();

        try {
            run(settings, messageRouter, eventRouter, grpcRouter, resources);
        } catch (IncorrectDataFormat | CreatingConfigFileException e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(1);
        } catch (ConfigError e) {
            LOGGER.error("Failed to load file with session settings", e);
            System.exit(1);
        }

    }

    public static void run(Settings settings, MessageRouter<MessageGroupBatch> messageRouter, MessageRouter<EventBatch> eventRouter,
                           GrpcRouter grpcRouter, Deque<Resources> resources) throws CreatingConfigFileException, ConfigError, IncorrectDataFormat {

        File configFile = FixBeanUtil.createConfig(settings);

        StringBuilder stringBuilder = new StringBuilder();
        Map<SessionID, ConnectionID> connections = new HashMap<>();
        Map<String, SessionID> sessionIDS = new HashMap<>();

        for (int i = 0; i < settings.sessionsSettings.size(); i++) {

            FixBean fixBean = settings.sessionsSettings.get(i);
            String sessionAlias = fixBean.getSessionAlias();
            SessionID sessionID = FixBeanUtil.getSessionID(fixBean);
            sessionIDS.put(sessionAlias, sessionID);
            connections.put(sessionID, ConnectionID.newBuilder().setSessionAlias(sessionAlias).build());
            stringBuilder.append(sessionAlias);

            if (i < settings.sessionsSettings.size() - 1) {
                stringBuilder.append(":");
            }
        }

        Event curEvent = MessageRouterUtils.storeEvent(eventRouter, Event.start(), null);
        curEvent.name("FIX client " + stringBuilder + " " + Instant.now());
        curEvent.type("Microservice");
        String rootEventId = curEvent.getId();

        FixClient fixClient = new FixClient(new SessionSettings(configFile.getAbsolutePath()),
                messageRouter, eventRouter, connections, rootEventId);

        configFile.deleteOnExit();
        resources.add(new Resources("client", fixClient::stop));

        ClientController controller = new ClientController(fixClient);

        MessageListener<MessageGroupBatch> listener = (consumerTag, groupBatch) -> {
            if (!controller.isRunning()) controller.start(settings.autoStopAfter);

            groupBatch.getGroupsList().forEach((group) -> {
                try {
                    if (group.getMessagesCount() != 1) {
                        if (LOGGER.isErrorEnabled()) {
                            LOGGER.error("Message group contains more than 1 message {} ", toJson(group));
                        }
                    } else {
                        AnyMessage message = group.getMessagesList().get(0);
                        if (!message.hasRawMessage()) {
                            if (LOGGER.isErrorEnabled()) {
                                LOGGER.error("Message in the group is not a raw message {} ", toJson(message));
                            }
                        } else {
                            String sessionAlias = MessageUtil.getSessionAlias(message);
                            String strMessage = MessageUtil.rawToString(message);
                            Session session = Session.lookupSession(sessionIDS.get(sessionAlias));

                            Message fixMessage = MessageUtils.parse(session, strMessage);
                            session.send(fixMessage);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to handle message group: {}", toJson(group), e);
                    MessageRouterUtils.storeEvent(eventRouter, rootEventId, "Failed to handle message group: " + toJson(group), "Error", e);
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
        @JsonProperty(required = true)
        boolean grpcStartControl = false;
        @JsonProperty(required = true)
        boolean autoStart = true;
        @JsonProperty(required = true)
        int autoStopAfter = 0;
        @JsonProperty(required = true)
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
            if (autoStopAfter < 0) {
                throw new IllegalArgumentException("Timer for automatically stopping the client cannot be negative (value of timer: " + autoStopAfter + ")");
            }
            this.autoStopAfter = autoStopAfter;
        }

        public List<FixBean> getSessionsSettings() throws IncorrectDataFormat {
            Set<String> sessionIds = new HashSet<>(); // if the session IDs or session aliases are not unique, we will get an error
            Set<String> sessionAliases = new HashSet<>();

            for (FixBean fixBean : sessionsSettings) {
                sessionIds.add(FixBeanUtil.getSessionID(fixBean).toString());
                sessionAliases.add(fixBean.getSessionAlias());
            }

            if (sessionIds.size() != sessionsSettings.size() || sessionAliases.size() != sessionsSettings.size()) {
                throw new IncorrectDataFormat("SessionID and SessionAlias in sessions settings should be unique.");
            }

            return sessionsSettings;
        }

        @Override
        public String toString() {

            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                    .append("grpcStartControl", grpcStartControl)
                    .append("autoStart", autoStart)
                    .append("autoStopAfter", autoStopAfter)
                    .append("FileStorePath", fileStorePath)
                    .append("FileLogPath", fileLogPath)
                    .append("ConnectionType", connectionType)
                    .append("ReconnectInterval", reconnectInterval)
                    .append("HeartBtInt", heartBtInt)
                    .append("UseDataDictionary", useDataDictionary)
                    .append("ValidateUserDefinedFields", validateUserDefinedFields)
                    .append("ValidateIncomingMessage", validateIncomingMessage)
                    .append("RefreshOnLogon", refreshOnLogon)
                    .append("NonStopSession", nonStopSession)
                    .append("BeginString", beginString)
                    .append("SocketConnectHost", socketConnectHost)
                    .append("SocketConnectPort", socketConnectPort)
                    .append("SenderCompID", senderCompID)
                    .append("SenderSubID", senderSubID)
                    .append("SenderLocationID", senderLocationID)
                    .append("TargetCompID", targetCompID)
                    .append("TargetSubID", targetSubID)
                    .append("TargetLocationID", targetLocationID)
                    .append("DataDictionary", dataDictionary)
                    .append("SessionQualifier", sessionQualifier)
                    .append("SessionAlias", sessionAlias)
                    .append("sessionsSettings", sessionsSettings)
                    .toString();
        }
    }
}
