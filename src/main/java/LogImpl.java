import com.exactpro.th2.common.grpc.ConnectionID;
import com.exactpro.th2.common.grpc.Direction;
import com.exactpro.th2.common.grpc.EventBatch;
import com.exactpro.th2.common.grpc.MessageGroupBatch;
import com.exactpro.th2.common.schema.message.MessageRouter;
import com.exactpro.th2.common.schema.message.MessageRouterUtils;
import com.exactpro.th2.common.schema.message.QueueAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.Log;
import util.MessageUtil;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.SECONDS;

public class LogImpl implements Log { //todo mb implement AbstractLog?

    private final Logger logger = LoggerFactory.getLogger(LogImpl.class);

    private final Log log;
    private final MessageRouter<MessageGroupBatch> messageRouter;
    private final MessageRouter<EventBatch> eventRouter;
    private final ConnectionID connectionID;
    private final String rootEventId;

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
//        System.out.println("get a message from server: " + message);

        try {
            onMessage(messageRouter, message.getBytes(), connectionID, Direction.FIRST);
        } catch (IOException e) {
            logger.error("error occur on incoming message");
        }

    }

    @Override
    public void onOutgoing(String message) {
        log.onOutgoing(message);

        try {
            onMessage(messageRouter, message.getBytes(), connectionID, Direction.SECOND);
        } catch (Exception e) {
            logger.error("error occur on outgoing message");
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
        onEvent(new Exception(), eventRouter, rootEventId, text);
    }


    long incomingSequence = createSequence();
    long outgoingSequence = createSequence();

    public void onMessage(MessageRouter<MessageGroupBatch> messageRouter, byte[] message,
                          ConnectionID connectionID, Direction direction) throws IOException {
        long sequence = direction == Direction.FIRST ? incomingSequence : outgoingSequence;
        QueueAttribute attribute = direction == Direction.FIRST ? QueueAttribute.FIRST : QueueAttribute.SECOND;
        messageRouter.send(MessageUtil.toBatch(message, connectionID, direction, sequence), attribute.toString());

    }

    public void onEvent(Throwable cause, MessageRouter<EventBatch> eventRouter, String rootEventId, String message) {
        String type = cause != null ? "Error" : "Info";
        MessageRouterUtils.storeEvent(eventRouter, rootEventId, message, type, cause);
    }

    private Long createSequence() {
        Instant instant = Instant.now();
        return (new AtomicLong(instant.getEpochSecond() * SECONDS.toNanos(1) + instant.getNano())).incrementAndGet();

    }
}
