import com.exactpro.th2.common.grpc.*;
import com.exactpro.th2.common.schema.grpc.router.GrpcRouter;
import com.exactpro.th2.common.schema.message.MessageListener;
import com.exactpro.th2.common.schema.message.MessageRouter;
import com.exactpro.th2.common.schema.message.MessageRouterContext;
import com.exactpro.th2.common.schema.message.SubscriberMonitor;
import com.exactpro.th2.common.schema.message.configuration.MessageRouterConfiguration;
import com.exactpro.th2.common.schema.message.impl.rabbitmq.connection.ConnectionManager;
import com.google.protobuf.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.mockito.Mockito;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix42.NewOrderSingle;

import javax.management.JMException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class MainTest {

    @Test
    public void runTest() throws Exception {

        Main.Settings settings = new Main.Settings();

        MyMessageRouter messageRouter = new MyMessageRouter();

        MessageRouter<EventBatch> eventRouter = new MyEventRouter();
        GrpcRouter grpcRouter = Mockito.mock(GrpcRouter.class);
        ConcurrentLinkedDeque<Main.Resources> resources = new ConcurrentLinkedDeque<>();

        Thread thread = new Thread(() -> {
            try {
                Main.run(settings, messageRouter, eventRouter, grpcRouter, resources);
            } catch (ConfigError | FieldConvertError | JMException configError) {
                configError.printStackTrace();
            }
        });
        thread.start();

        NewOrderSingle fixMessage = new NewOrderSingle(
                new ClOrdID("1"),
                new HandlInst('1'),
                new Symbol("ClientXXXMMMMMMM"),
                new Side('1'),
                new TransactTime(LocalDateTime.now()),
                new OrdType('1'));
        // market order fields
        fixMessage.set(new OrderQty(1));
        fixMessage.set(new Price(10.0));

        MessageGroupBatch messageGroupBatch = MessageGroupBatch.newBuilder()
                .addGroups(MessageGroup.newBuilder()
                        .addMessages(AnyMessage.newBuilder()
                                .setRawMessage(RawMessage.newBuilder()
                                        .setBody(ByteString
                                                .copyFrom(fixMessage
                                                        .toString()
                                                        .getBytes()))
                                        .build())
                                .build())
                        .build())
                .build();

        Thread.sleep(10000);
        System.out.println("ten second left");

        System.out.println(messageRouter.listOfListeners.size() + " quantity of listeners");

        messageRouter.sendToSubscriber("xmm", messageGroupBatch);

        Thread.sleep(10000);

        for (MessageGroupBatch item : messageRouter.listOfMessages) {
            System.out.println(item);
        }


        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        try {
            lock.lock();
            condition.await();
            lock.unlock();
        } catch (InterruptedException e) {
            System.out.println("Interrupted Exception in main");
        }

    }

    public static class MyMessageRouter implements MessageRouter<MessageGroupBatch> {

        List<MessageListener<MessageGroupBatch>> listOfListeners = new ArrayList<>();
        List<MessageGroupBatch> listOfMessages = new ArrayList<>();

        public void sendToSubscriber(String tag, MessageGroupBatch message) throws Exception {
            listOfListeners.get(0).handler(tag, message);
        }

        @Override
        public void init(@NotNull ConnectionManager connectionManager, @NotNull MessageRouterConfiguration configuration) {

        }

        @Override
        public void init(@NotNull MessageRouterContext context) {

        }

        @Override
        public void send(MessageGroupBatch message) {

            listOfMessages.add(message);
        }

        @Override
        public void send(MessageGroupBatch message, String... queueAttr) throws IOException {
            listOfMessages.add(message);
        }

        @Override
        public void sendAll(MessageGroupBatch message, String... queueAttr) throws IOException {
            listOfMessages.add(message);
        }

        @Override
        public @Nullable SubscriberMonitor subscribe(MessageListener callback, String... queueAttr) {

            listOfListeners.add(callback);

            return () -> {
            };
        }

        @Override
        public @Nullable SubscriberMonitor subscribeAll(MessageListener callback) {
            listOfListeners.add(callback);

            return () -> {
            };
        }

        @Override
        public @Nullable SubscriberMonitor subscribeAll(MessageListener callback, String... queueAttr) {
            listOfListeners.add(callback);

            return () -> {
            };
        }

        @Override
        public void close() throws Exception {

        }

    }

    public static class MyEventRouter implements MessageRouter<EventBatch> {
        @Override
        public void init(@NotNull ConnectionManager connectionManager, @NotNull MessageRouterConfiguration configuration) {

        }

        @Override
        public void init(@NotNull MessageRouterContext context) {

        }

        @Override
        public @Nullable SubscriberMonitor subscribe(MessageListener<EventBatch> callback, String... queueAttr) {
            return null;
        }

        @Override
        public @Nullable SubscriberMonitor subscribeAll(MessageListener<EventBatch> callback) {
            return null;
        }

        @Override
        public @Nullable SubscriberMonitor subscribeAll(MessageListener<EventBatch> callback, String... queueAttr) {
            return null;
        }

        @Override
        public void send(EventBatch message) throws IOException {

        }

        @Override
        public void send(EventBatch message, String... queueAttr) throws IOException {

        }

        @Override
        public void sendAll(EventBatch message, String... queueAttr) throws IOException {

        }

        @Override
        public void close() throws Exception {

        }
    }


}
