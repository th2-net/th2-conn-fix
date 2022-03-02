package com.exactpro.th2.fix.client;

import com.exactpro.th2.common.event.Event;
import com.exactpro.th2.common.grpc.ConnectionID;
import com.exactpro.th2.common.grpc.EventBatch;
import com.exactpro.th2.common.grpc.MessageGroupBatch;
import com.exactpro.th2.common.schema.message.MessageRouter;
import com.exactpro.th2.common.schema.message.MessageRouterUtils;
import quickfix.Log;
import quickfix.LogFactory;
import quickfix.SessionID;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class LogFactoryImpl implements LogFactory {

    private final MessageRouter<MessageGroupBatch> messageRouter;
    private final MessageRouter<EventBatch> eventRouter;
    private final LogFactory logFactory;
    private final Map<SessionID, ConnectionID> connections;
    private final String rootEventId;

    public LogFactoryImpl(LogFactory logFactory, MessageRouter<MessageGroupBatch> messageRouter, MessageRouter<EventBatch> eventRouter,
                          Map<SessionID, ConnectionID> connections, String rootEventId) {
        this.logFactory = logFactory;
        this.messageRouter = messageRouter;
        this.eventRouter = eventRouter;
        this.connections = connections;
        this.rootEventId = rootEventId;
    }

    @Override
    public Log create(SessionID sessionID) {
        ConnectionID connectionID = Objects.requireNonNull(connections.get(sessionID), () -> "Unknown session ID: " + sessionID);
        
        String eventName = "Fix client " + connectionID.getSessionAlias() + " " + Instant.now();
        Event event = MessageRouterUtils.storeEvent(eventRouter, rootEventId, eventName, "Microservice", null);

        return new LogImpl(logFactory.create(sessionID), messageRouter, eventRouter, connectionID, event.getId());
    }

}
