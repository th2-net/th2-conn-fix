package com.exactpro.th2.fix.client;

import com.exactpro.th2.common.grpc.ConnectionID;
import com.exactpro.th2.common.grpc.Direction;
import com.exactpro.th2.common.grpc.EventBatch;
import com.exactpro.th2.common.grpc.MessageGroupBatch;
import com.exactpro.th2.common.schema.message.MessageRouter;
import com.exactpro.th2.common.schema.message.MessageRouterUtils;
import com.exactpro.th2.common.schema.message.QueueAttribute;
import com.exactpro.th2.fix.client.util.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.Log;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.SECONDS;

public class LogImpl implements Log {

    private final Logger LOGGER = LoggerFactory.getLogger(LogImpl.class);

    private final Log log;
    private final MessageRouter<MessageGroupBatch> messageRouter;
    private final MessageRouter<EventBatch> eventRouter;
    private final ConnectionID connectionID;
    private final String rootEventId;
    private final Supplier<Long> inputSeq = createSequence();
    private final Supplier<Long> outputSeq = createSequence();

    public LogImpl(Log log, MessageRouter<MessageGroupBatch> messageRouter, MessageRouter<EventBatch> eventRouter,
                   ConnectionID connectionID, String rootEventId) {
        this.log = log;
        this.messageRouter = messageRouter;
        this.eventRouter = eventRouter;
        this.connectionID = connectionID;
        this.rootEventId = rootEventId;
    }


    @Override
    public void clear() {
        log.clear();
    }

    @Override
    public void onIncoming(String message) {
        log.onIncoming(message);

        try {
            onMessage(message, Direction.FIRST);
        } catch (Exception e) {
            sendError(connectionID.getSessionAlias(), e);
        }
    }

    @Override
    public void onOutgoing(String message) {
        log.onOutgoing(message);

        try {
            onMessage(message, Direction.SECOND);
        } catch (Exception e) {
            sendError(connectionID.getSessionAlias(), e);
        }
    }

    private void sendError(String sessionAlias, Exception e) {
        LOGGER.error("Failed to send message for sessionAlias: {}", sessionAlias, e);
        onErrorEvent("Failed to send message for sessionAlias: " + sessionAlias, e);
    }

    @Override
    public void onEvent(String text) {
        log.onEvent(text);
        MessageRouterUtils.storeEvent(eventRouter, rootEventId, text, "Info", null);

    }

    @Override
    public void onErrorEvent(String text) {
        log.onErrorEvent(text);
        MessageRouterUtils.storeEvent(eventRouter, rootEventId, text, "Error", null);
    }

    public void onErrorEvent(String text, Throwable e) {
        log.onErrorEvent(text);
        MessageRouterUtils.storeEvent(eventRouter, rootEventId, text, "Error", e);
    }

    private void onMessage(String message, Direction direction) throws IOException {
        Supplier<Long> sequence = direction == Direction.FIRST ? inputSeq : outputSeq;
        QueueAttribute attribute = direction == Direction.FIRST ? QueueAttribute.FIRST : QueueAttribute.SECOND;
        messageRouter.send(MessageUtil.toBatch(message.getBytes(), connectionID, direction, sequence.get()), attribute.toString());
    }

    private static Supplier<Long> createSequence() {
        Instant instant = Instant.now();
        return (new AtomicLong(instant.getEpochSecond() * SECONDS.toNanos(1) + instant.getNano()))::incrementAndGet;
    }
}
