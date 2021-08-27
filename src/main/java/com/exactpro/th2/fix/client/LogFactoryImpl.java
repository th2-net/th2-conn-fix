package com.exactpro.th2.fix.client;

import com.exactpro.th2.common.grpc.ConnectionID;
import com.exactpro.th2.common.grpc.EventBatch;
import com.exactpro.th2.common.grpc.MessageGroupBatch;
import com.exactpro.th2.common.schema.message.MessageRouter;
import quickfix.Log;
import quickfix.LogFactory;
import quickfix.SessionID;

import java.util.Map;

public class LogFactoryImpl implements LogFactory {

    private final MessageRouter<MessageGroupBatch> messageRouter;
    private final MessageRouter<EventBatch> eventBatch;
    private final LogFactory logFactory;
    private final Map<SessionID, ConnectionID> connections;
    private final String rootEventId;

    public LogFactoryImpl(LogFactory logFactory, MessageRouter<MessageGroupBatch> messageRouter, MessageRouter<EventBatch> eventRouter,
                          Map<SessionID, ConnectionID> connections, String rootEventId) {
        this.logFactory = logFactory;
        this.messageRouter = messageRouter;
        this.eventBatch = eventRouter;
        this.connections = connections;
        this.rootEventId = rootEventId;
    }

    @Override
    public Log create(SessionID sessionID) {
        return new LogImpl(logFactory.create(sessionID), messageRouter, eventBatch, connections.get(sessionID), rootEventId);
    }

}
