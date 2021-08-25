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
import quickfix.SessionID;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.SECONDS;

public class LogImpl implements Log {

    private final Logger LOGGER = LoggerFactory.getLogger(LogImpl.class);

    private final Log log;
    private final MessageRouter<MessageGroupBatch> messageRouter;
    private final MessageRouter<EventBatch> eventRouter;
    private final List<ConnectionID> connectionIDS;
    private final String rootEventId;

    public LogImpl(Log log, MessageRouter<MessageGroupBatch> messageRouter, MessageRouter<EventBatch> eventRouter,
                   List<ConnectionID> connectionIDS, String rootEventId) {
        this.log = log;
        this.messageRouter = messageRouter;
        this.eventRouter = eventRouter;
        this.connectionIDS = connectionIDS;
        this.rootEventId = rootEventId;
    }


    @Override
    public void clear() {
        log.clear();
    }

    @Override
    public void onIncoming(String message) {
        log.onIncoming(message);

        String sessionAlias = createSessionAlias(message, Direction.FIRST);
        try {
            onMessage(messageRouter, message.getBytes(), getConnectionId(sessionAlias), Direction.FIRST);
        } catch (IOException e) {
            LOGGER.error("Failed to store incoming message: {}", message);
            onErrorEvent(message, e);
        }

    }


    @Override
    public void onOutgoing(String message) {
        log.onOutgoing(message);

        String sessionAlias = createSessionAlias(message, Direction.SECOND);
        try {
            onMessage(messageRouter, message.getBytes(), getConnectionId(sessionAlias), Direction.SECOND);
        } catch (Exception e) {
            LOGGER.error("Failed to send outgoing message: {}", message);
            onErrorEvent(message, e);
        }
    }


    @Override
    public void onEvent(String text) {

        log.onEvent(text);
        onEvent(null, eventRouter, rootEventId, text);
    }

    @Override
    public void onErrorEvent(String text) {

        log.onEvent(text);
    }

    public void onErrorEvent(String text, Throwable e) {
        log.onEvent(text);
        onEvent(e, eventRouter, rootEventId, text);
    }

    Supplier<Long> inputSeq = createSequence();
    Supplier<Long> outputSeq = createSequence();

    public void onMessage(MessageRouter<MessageGroupBatch> messageRouter, byte[] message,
                          ConnectionID connectionID, Direction direction) throws IOException {
        Supplier<Long> sequence = direction == Direction.FIRST ? inputSeq : outputSeq;
        QueueAttribute attribute = direction == Direction.FIRST ? QueueAttribute.FIRST : QueueAttribute.SECOND;
        messageRouter.send(MessageUtil.toBatch(message, connectionID, direction, sequence.get()), attribute.toString());

    }

    public void onEvent(Throwable cause, MessageRouter<EventBatch> eventRouter, String rootEventId, String message) {
        String type = cause != null ? "Error" : "Info";
        MessageRouterUtils.storeEvent(eventRouter, rootEventId, message, type, cause);
    }

    private Supplier<Long> createSequence() {
        Instant instant = Instant.now();
        return (new AtomicLong(instant.getEpochSecond() * SECONDS.toNanos(1) + instant.getNano()))::incrementAndGet;

    }

    private String createSessionAlias(String message, Direction direction) { //todo rethink the logic
        SessionID sessionID = MessageUtil.getSessionID(message);
        String sessionAlias = "";

        if (direction == Direction.FIRST) {
            sessionAlias = sessionID.getBeginString()
                    .concat(":")
                    .concat(sessionID.getTargetCompID())
                    .concat("->")
                    .concat(sessionID.getSenderCompID());
        } else {
            sessionAlias = sessionID.getBeginString()
                    .concat(":")
                    .concat(sessionID.getSenderCompID())
                    .concat("->")
                    .concat(sessionID.getTargetCompID());
        }
        return sessionAlias;
    }

    private ConnectionID getConnectionId(String sessionAlias) {

        return connectionIDS.stream()
                .filter(x -> x.getSessionAlias().equals(sessionAlias))
                .findFirst()
                .orElseThrow(NullPointerException::new);
    }
}
