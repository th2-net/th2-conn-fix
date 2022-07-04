package com.exactpro.th2.fix.client;

import com.exactpro.th2.common.grpc.ConnectionID;
import com.exactpro.th2.common.grpc.Direction;
import com.exactpro.th2.common.grpc.RawMessage;
import com.exactpro.th2.common.utils.event.EventBatcher;
import com.exactpro.th2.common.utils.event.MessageBatcher;
import com.exactpro.th2.fix.client.util.EventUtil;
import com.exactpro.th2.fix.client.util.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.Log;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.SECONDS;

public class LogImpl implements Log {

    private final Logger LOGGER = LoggerFactory.getLogger(LogImpl.class);

    private final EventBatcher eventBatcher;
    private final ConnectionID connectionID;
    private final String parentEventId;
    private final String sessionAlias;
    private final Supplier<Long> inputSeq = createSequence();
    private final Supplier<Long> outputSeq = createSequence();
    private final MessageBatcher messageBatcher;


    public LogImpl(MessageBatcher messageBatcher, EventBatcher eventBatcher,
                   ConnectionID connectionID, String parentEventId) {
        this.messageBatcher = messageBatcher;
        this.eventBatcher = eventBatcher;
        this.connectionID = connectionID;
        this.parentEventId = parentEventId;
        this.sessionAlias = connectionID.getSessionAlias();
    }

    @Override
    public void clear() {
    }

    @Override
    public void onIncoming(String message) {
        try {
            onMessage(message, Direction.FIRST);
        } catch (Exception e) {
            sendError(sessionAlias, e);
        }
    }

    @Override
    public void onOutgoing(String message) {
        try {
            onMessage(message, Direction.SECOND);
        } catch (Exception e) {
            sendError(sessionAlias, e);
        }
    }

    private void sendError(String sessionAlias, Exception e) {
        String message = "Failed to send message for sessionAlias: " + sessionAlias;
        LOGGER.error(message, e);
        onErrorEvent(message, e);
    }

    @Override
    public void onEvent(String text) {
        eventBatcher.onEvent(EventUtil.toEvent(parentEventId, text));
    }

    @Override
    public void onErrorEvent(String text) {
        eventBatcher.onEvent(EventUtil.toEvent(parentEventId, text, "Error", null));
    }

    public void onErrorEvent(String text, Throwable e) {
        eventBatcher.onEvent(EventUtil.toEvent(parentEventId, text, e));
    }

    private void onMessage(String message, Direction direction) {
        Supplier<Long> sequence = direction == Direction.FIRST ? inputSeq : outputSeq;

        RawMessage rawMessage = MessageUtil.toRawMessage(message.getBytes(), connectionID, direction, sequence.get());
        messageBatcher.onMessage(rawMessage, direction);

        String parentEventID = FixMessage.getMessageParentEventId(message) == null ? parentEventId : FixMessage.getMessageParentEventId(message).getId();
        eventBatcher.onEvent(EventUtil.toEvent(rawMessage, parentEventID, "Message successfully sent"));
    }

    private static Supplier<Long> createSequence() {
        Instant instant = Instant.now();
        return (new AtomicLong(instant.getEpochSecond() * SECONDS.toNanos(1) + instant.getNano()))::incrementAndGet;
    }
}