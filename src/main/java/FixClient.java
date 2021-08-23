import com.exactpro.th2.common.grpc.ConnectionID;
import com.exactpro.th2.common.grpc.EventBatch;
import com.exactpro.th2.common.grpc.MessageGroupBatch;
import com.exactpro.th2.common.schema.message.MessageRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import service.ServerApplication;

import javax.management.JMException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class FixClient {

    private static final Logger log = LoggerFactory.getLogger(FixClient.class);

    private final SocketInitiator initiator;
    private final ServerApplication application;
    private volatile boolean isRunning = false;


    public FixClient(SessionSettings settings, MessageRouter<MessageGroupBatch> messageRouter, MessageRouter<EventBatch> eventRouter,
                     ConnectionID connectionID, String rootEventId) throws ConfigError, FieldConvertError, JMException {

        application = new ServerApplication();
        MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new LogFactoryImpl(new FileLogFactory(settings), messageRouter, eventRouter, connectionID, rootEventId);
        MessageFactory messageFactory = new DefaultMessageFactory();

        initiator = new SocketInitiator(application, messageStoreFactory, settings, logFactory, messageFactory);

    }


    public void start() {// todo should we receive lock or create new one?
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        try {
            lock.lock();
            initiator.start();
        } catch (Exception e) {
            log.error("error occur when starting FixClient ", e);
        } finally {
            lock.unlock();
        }

    }

    public void stop() {

        isRunning = false;
        initiator.stop();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public ServerApplication getApplication() {
        return application;
    }
}
