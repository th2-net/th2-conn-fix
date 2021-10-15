package com.exactpro.th2.fix.client;

import com.exactpro.th2.common.grpc.ConnectionID;
import com.exactpro.th2.common.grpc.EventBatch;
import com.exactpro.th2.common.grpc.MessageGroupBatch;
import com.exactpro.th2.common.schema.message.MessageRouter;
import com.exactpro.th2.fix.client.service.ClientApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.ThreadedSocketInitiator;

import java.util.Map;


public class FixClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(FixClient.class);

    private final ThreadedSocketInitiator initiator;
    private volatile boolean isRunning = false;


    public FixClient(SessionSettings settings, MessageRouter<MessageGroupBatch> messageRouter, MessageRouter<EventBatch> eventRouter,
                     Map<SessionID, ConnectionID> connections, String rootEventId, int queueCapacity) throws ConfigError {

        ClientApplication application = new ClientApplication();
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new LogFactoryImpl(new FileLogFactory(settings), messageRouter, eventRouter, connections, rootEventId);
        MessageFactory messageFactory = new DefaultMessageFactory();

        initiator = new ThreadedSocketInitiator(application, messageStoreFactory, settings, logFactory, messageFactory, queueCapacity);

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
