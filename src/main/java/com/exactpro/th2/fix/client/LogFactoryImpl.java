package com.exactpro.th2.fix.client;

import com.exactpro.th2.common.grpc.ConnectionID;
import com.exactpro.th2.common.utils.event.EventBatcher;
import com.exactpro.th2.common.utils.event.MessageBatcher;
import quickfix.Log;
import quickfix.LogFactory;
import quickfix.SessionID;

import java.util.Map;
import java.util.Objects;

public class LogFactoryImpl implements LogFactory {

    private final EventBatcher eventBatcher;
    private final LogFactory logFactory;
    private final Map<SessionID, ConnectionID> connectionIds;
    private final Map<SessionID, String> sessionsEvents;
    private final MessageBatcher messageBatcher;

    public LogFactoryImpl(LogFactory logFactory, MessageBatcher messageBatcher, EventBatcher eventBatcher,
                          Map<SessionID, String> sessionsEvents, Map<SessionID, ConnectionID> connectionIds) {
        this.logFactory = logFactory;
        this.eventBatcher = eventBatcher;
        this.connectionIds = connectionIds;
        this.sessionsEvents = sessionsEvents;
        this.messageBatcher = messageBatcher;
    }

    @Override
    public Log create(SessionID sessionID) {
        ConnectionID connectionID = Objects.requireNonNull(connectionIds.get(sessionID), () -> "Unknown session ID: " + sessionID);
        return new LogImpl(logFactory.create(sessionID), messageBatcher, eventBatcher, connectionID, sessionsEvents.get(sessionID));
    }

}
