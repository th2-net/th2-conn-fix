package com.exactpro.th2.fix.client;

import com.exactpro.th2.common.grpc.ConnectionID;
import com.exactpro.th2.common.utils.event.EventBatcher;
import com.exactpro.th2.common.utils.event.MessageBatcher;
import com.exactpro.th2.fix.client.service.ClientApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.ConfigError;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.NoopStoreFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;

import java.util.Map;


public class FixClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(FixClient.class);

    private final SocketInitiator initiator;
    private volatile boolean isRunning = false;


    public FixClient(SessionSettings settings, Main.Settings sessionsSettings, MessageBatcher messageBatcher, EventBatcher eventBatcher,
                     Map<SessionID, ConnectionID> connectionIds, Map<SessionID, String> sessionEvents, int queueCapacity) throws ConfigError {

        ClientApplication application = new ClientApplication(sessionsSettings);
        MessageStoreFactory messageStoreFactory = new NoopStoreFactory();
        LogFactory logFactory = new LogFactoryImpl(messageBatcher, eventBatcher, sessionEvents, connectionIds);
        MessageFactory messageFactory = new FixMessageFactory();
        initiator = new SocketInitiator(application, messageStoreFactory, settings, logFactory, messageFactory, queueCapacity);
    }

    public synchronized void start() {
        try {
            initiator.start();
            isRunning = true;
        } catch (Exception e) {
            LOGGER.error("Failed to start client", e);
        }
    }

    public synchronized void stop() {
        try {
            isRunning = false;
            initiator.stop();
        } catch (Exception e) {
            LOGGER.error("Failed to stop client", e);
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
}