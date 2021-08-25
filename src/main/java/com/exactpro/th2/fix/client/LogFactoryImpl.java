package com.exactpro.th2.fix.client;

import com.exactpro.th2.common.grpc.ConnectionID;
import com.exactpro.th2.common.grpc.EventBatch;
import com.exactpro.th2.common.grpc.MessageGroupBatch;
import com.exactpro.th2.common.schema.message.MessageRouter;
import quickfix.Log;
import quickfix.LogFactory;
import quickfix.SessionID;

import java.util.List;

public class LogFactoryImpl implements LogFactory {

    private final MessageRouter<MessageGroupBatch> messageRouter;
    private final MessageRouter<EventBatch> eventBatch;
    private final LogFactory logFactory;
    private final List<ConnectionID> connectionIDS;
    private final String rootEventId;

        public LogFactoryImpl(LogFactory logFactory, MessageRouter<MessageGroupBatch> messageRouter, MessageRouter<EventBatch> eventRouter,
                          List<ConnectionID> connectionIDS, String rootEventId) {
        this.logFactory = logFactory;
        this.messageRouter = messageRouter;
        this.eventBatch = eventRouter;
        this.connectionIDS = connectionIDS;
        this.rootEventId = rootEventId;
    }

    @Override
    public Log create(SessionID sessionID) {
        return new LogImpl(logFactory.create(sessionID), messageRouter, eventBatch, connectionIDS, rootEventId);
    }

}
