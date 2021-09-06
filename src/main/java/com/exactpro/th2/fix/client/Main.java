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
import com.exactpro.th2.fix.client.fixBean.BaseFixBean;
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
    private static final Pattern DICTIONARY_FILE_NAME = Pattern.compile("FIX\\.[4-5]\\.[0-4]\\.xml");

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

        Map<String, File> dictionaries = new HashMap<>();

        try (ZipInputStream zin = new ZipInputStream(factory.readDictionary())) {
            ZipEntry zipEntry;
            while ((zipEntry = zin.getNextEntry()) != null) {
                String zipName = zipEntry.getName();
                if (DICTIONARY_FILE_NAME.matcher(zipName).matches()) {
                    throw new IncorrectFixFileNameException("Incorrect file name for FIX dictionary");
                }
                File dataDict = File.createTempFile(zipEntry.getName(), "");
                Files.write(dataDict.toPath(), zin.readAllBytes());
                new DataDictionary(dataDict.getAbsolutePath()); //check that xml file contains the correct values
                dictionaries.put(zipName.replaceFirst(".xml", ""), dataDict);
                dataDict.deleteOnExit();
            }
        } catch (IOException | ConfigError e) {
            throw new Exception("Failed to create DataDictionary", e);
        }

        for (FixBean fixBean : settings.sessionSettings) {
            for (Map.Entry<String, File> dictionaryEntry : dictionaries.entrySet()) {
                if (dictionaryEntry.getKey().equals(fixBean.getBeginString())) {
                    fixBean.setDataDictionary(dictionaryEntry.getValue().getAbsolutePath());
                }
            }
            if (fixBean.getDataDictionary() == null) {
                throw new EmptyDataDictionaryException("No dictionary for: " + fixBean.getBeginString());
            }
        }

        MessageRouter<EventBatch> eventRouter = factory.getEventBatchRouter();
        MessageRouter<MessageGroupBatch> messageRouter = factory.getMessageRouterMessageGroupBatch();
        GrpcRouter grpcRouter = factory.getGrpcRouter();

        try {
            run(settings, messageRouter, eventRouter, grpcRouter, resources);
        } catch (IncorrectDataFormat | CreatingConfigFileException e) {
            LOGGER.error("Error when using the config file", e);
            System.exit(1);
        } catch (ConfigError e) {
            LOGGER.error("Failed to load file with session settings", e);
            System.exit(1);
        }

    }

    public static void run(Settings settings, MessageRouter<MessageGroupBatch> messageRouter, MessageRouter<EventBatch> eventRouter,
                           GrpcRouter grpcRouter, Deque<Resources> resources) throws CreatingConfigFileException, ConfigError, IncorrectDataFormat {

        File configFile = FixBeanUtil.createConfig(settings);
        System.out.println(settings);

        Map<SessionID, ConnectionID> connectionIDs = new HashMap<>();
        Map<String, SessionID> sessionIDs = Settings.getSessionIDs(settings.sessionSettings);

        for (Map.Entry<String, SessionID> sessionIDEntry : sessionIDs.entrySet()) {
            String sessionAlias = sessionIDEntry.getKey();
            SessionID sessionID = sessionIDEntry.getValue();
            connectionIDs.put(sessionID, ConnectionID.newBuilder().setSessionAlias(sessionAlias).build());
        }

        Event curEvent = MessageRouterUtils.storeEvent(eventRouter, Event.start(), null);
        curEvent.name("FIX client " + sessionIDs.keySet().stream()
                .reduce((resultKey, nextKey) -> resultKey + nextKey)
                .orElse("")
                + " " + Instant.now());
        curEvent.type("Microservice");
        String rootEventId = curEvent.getId();

        FixClient fixClient = new FixClient(new SessionSettings(configFile.getAbsolutePath()),
                messageRouter, eventRouter, connectionIDs, rootEventId, settings.queueCapacity);

        configFile.deleteOnExit();
        resources.add(new Resources("client", fixClient::stop));

        ClientController controller = new ClientController(fixClient);

        MessageListener<MessageGroupBatch> listener = (consumerTag, groupBatch) -> {
            if (!controller.isRunning()) controller.start(settings.autoStopAfter);

            groupBatch.getGroupsList().forEach((group) -> {
                try {
                    if (group.getMessagesCount() != 1) {
                        if (LOGGER.isErrorEnabled()) {
                            LOGGER.error("Message group contains more or less than 1 message {} ", toJson(group));
                        }
                    } else {
                        AnyMessage message = group.getMessagesList().get(0);
                        if (!message.hasRawMessage()) {
                            if (LOGGER.isErrorEnabled()) {
                                LOGGER.error("Message in the group is not a raw message {} ", toJson(message));
                            }
                            return;
                        }
                        String sessionAlias = MessageUtil.getSessionAlias(message);
                        if (sessionAlias == null || sessionAlias.isBlank()) {
                            throw new IllegalArgumentException("No such session alias for message: " + message);
                        }
                        String strMessage = MessageUtil.rawToString(message);
                        Session session = Session.lookupSession(sessionIDs.get(sessionAlias));

                        Message fixMessage = MessageUtils.parse(session, strMessage);
                        session.send(fixMessage);

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

    public static class Settings extends BaseFixBean {
        boolean grpcStartControl = false;
        boolean autoStart = true;
        int autoStopAfter = 0;
        int queueCapacity = 1;
        @JsonProperty(required = true)
        List<FixBean> sessionSettings = new ArrayList<>();

        public static Map<String, SessionID> getSessionIDs(List<FixBean> sessionSettings) {

            if (sessionSettings.size() == 0) {
                throw new IllegalArgumentException("Session settings are empty.");
            }

            Map<String, SessionID> sessionIDs = new HashMap<>();

            for (FixBean fixBean : sessionSettings)
                sessionIDs.put(fixBean.getSessionAlias(), FixBeanUtil.getSessionID(fixBean));
            return sessionIDs;
        }

        public void setSessionSettings(List<FixBean> sessionSettings) throws IncorrectDataFormat {
            Set<SessionID> sessionIds = new HashSet<>(); // if the session IDs or session aliases are not unique, we will get an error
            Set<String> sessionAliases = new HashSet<>();

            for (FixBean fixBean : sessionSettings) {
                SessionID sessionID = FixBeanUtil.getSessionID(fixBean);
                String sessionAlias = fixBean.getSessionAlias();

                if (sessionIds.contains(sessionID) || sessionAliases.contains(sessionAlias)) {
                    throw new IncorrectDataFormat("SessionID and SessionAlias in sessions settings should be unique.");
                }

                sessionIds.add(sessionID);
                sessionAliases.add(sessionAlias);
            }
            this.sessionSettings = sessionSettings;
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

        @Override
        public void setFileStorePath(String fileStorePath) {
            throw new IllegalStateException("Set FileStorePath for default session settings are unavailable.");
        }

        public List<FixBean> getSessionSettings() {
            return sessionSettings;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }

        @Override
        public String toString() {

            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                    .appendSuper(super.toString())
                    .append("grpcStartControl", grpcStartControl)
                    .append("autoStart", autoStart)
                    .append("autoStopAfter", autoStopAfter)
                    .append("sessionsSettings", sessionSettings)
                    .toString();
        }
    }
}
