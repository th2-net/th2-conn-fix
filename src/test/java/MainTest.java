import com.exactpro.th2.common.grpc.AnyMessage;
import com.exactpro.th2.common.grpc.ConnectionID;
import com.exactpro.th2.common.grpc.EventBatch;
import com.exactpro.th2.common.grpc.MessageGroup;
import com.exactpro.th2.common.grpc.MessageGroupBatch;
import com.exactpro.th2.common.grpc.MessageID;
import com.exactpro.th2.common.grpc.RawMessage;
import com.exactpro.th2.common.grpc.RawMessageMetadata;
import com.exactpro.th2.common.schema.grpc.router.GrpcRouter;
import com.exactpro.th2.common.schema.message.MessageListener;
import com.exactpro.th2.common.schema.message.MessageRouter;
import com.exactpro.th2.common.schema.message.MessageRouterContext;
import com.exactpro.th2.common.schema.message.SubscriberMonitor;
import com.exactpro.th2.common.schema.message.configuration.MessageRouterConfiguration;
import com.exactpro.th2.common.schema.message.impl.rabbitmq.connection.ConnectionManager;
import com.exactpro.th2.fix.client.Main;
import com.exactpro.th2.fix.client.exceptions.CreatingConfigFileException;
import com.exactpro.th2.fix.client.fixBean.FixBean;
import com.google.protobuf.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.mockito.Mockito;
import quickfix.ConfigError;
import quickfix.IncorrectDataFormat;
import quickfix.Message;
import quickfix.field.BeginString;
import quickfix.field.ClOrdID;
import quickfix.field.HandlInst;
import quickfix.field.MsgType;
import quickfix.field.OrdType;
import quickfix.field.SenderCompID;
import quickfix.field.SenderSubID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TargetCompID;
import quickfix.field.TargetSubID;
import quickfix.field.TransactTime;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class MainTest extends Main {


    @Test
    public void runTest() throws Exception {

        Main.Settings settings = new Settings();

        FixBean fixBean = new FixBean();
        fixBean.setSenderCompID("client");
        fixBean.setTargetCompID("server");
        fixBean.setSenderSubID("sendSubId");
        fixBean.setTargetSubID("tarSubId");
        fixBean.setSessionAlias("FIX42ClientServer");

        FixBean fixBean1 = new FixBean();
        fixBean1.setSenderCompID("client2");
        fixBean1.setTargetCompID("server");
        fixBean1.setSocketConnectPort(9878);
        fixBean1.setSessionAlias("FIX42Client2Server");

        List<FixBean> fixBeans = new ArrayList<>();
        fixBeans.add(fixBean);
        fixBeans.add(fixBean1);
        settings.setSessionSettings(fixBeans);

        MyMessageRouter messageRouter = new MyMessageRouter();

        MessageRouter<EventBatch> eventRouter = new MyEventRouter();
        GrpcRouter grpcRouter = Mockito.mock(GrpcRouter.class);
        ConcurrentLinkedDeque<Main.Resources> resources = new ConcurrentLinkedDeque<>();

        Thread thread = new Thread(() -> {
            try {
                Main.run(settings, messageRouter, eventRouter, grpcRouter, resources);
            } catch (ConfigError | CreatingConfigFileException | IncorrectDataFormat configError) {
                configError.printStackTrace();
            }
        });
        thread.start();

        Message fixMessage = new Message();
        Message.Header header = fixMessage.getHeader();
        header.setField(new BeginString("FIX.4.2"));
        header.setField(new MsgType("D"));
        header.setField(new SenderCompID("client"));
        header.setField(new TargetCompID("server"));
        header.setField(new SenderSubID("sendSubId"));
        header.setField(new TargetSubID("tarSubId"));

        quickfix.fix42.NewOrderSingle fixMessage2 = new quickfix.fix42.NewOrderSingle(
                new ClOrdID("ClOrdID"),
                new HandlInst('3'),
                new Symbol("Symbol"),
                new Side('1'),
                new TransactTime(LocalDateTime.now()),
                new OrdType('1'));
        fixMessage2.setField(new SenderCompID("client2"));
        fixMessage2.setField(new TargetCompID("server"));

        MessageGroupBatch messageGroupBatch = MessageGroupBatch.newBuilder()
                .addGroups(MessageGroup.newBuilder()
                        .addMessages(AnyMessage.newBuilder()
                                .setRawMessage(RawMessage.newBuilder()
                                        .setBody(ByteString
                                                .copyFrom(fixMessage
                                                        .toString()
                                                        .getBytes()))
                                        .setMetadata(RawMessageMetadata
                                                .newBuilder()
                                                .setId(MessageID
                                                        .newBuilder()
                                                        .setConnectionId(ConnectionID
                                                                .newBuilder()
                                                                .setSessionAlias("FIX42ClientServer")
                                                                .build())
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();

        MessageGroupBatch messageGroupBatch2 = MessageGroupBatch.newBuilder()
                .addGroups(MessageGroup.newBuilder()
                        .addMessages(AnyMessage.newBuilder()
                                .setRawMessage(RawMessage.newBuilder()
                                        .setBody(ByteString
                                                .copyFrom(fixMessage2
                                                        .toString()
                                                        .getBytes()))
                                        .setMetadata(RawMessageMetadata
                                                .newBuilder()
                                                .setId(MessageID
                                                        .newBuilder()
                                                        .setConnectionId(ConnectionID
                                                                .newBuilder()
                                                                .setSessionAlias("FIX42Client2Server")
                                                                .build())
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();

        Thread.sleep(10000);

        messageRouter.sendToSubscriber("xmm", messageGroupBatch);
        messageRouter.sendToSubscriber("xmm2", messageGroupBatch2);

        Thread.sleep(10000);

        for (MessageGroupBatch item : messageRouter.messages) {
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

        List<MessageListener> listeners = new ArrayList<>();
        List<MessageGroupBatch> messages = new ArrayList<>();

        public void sendToSubscriber(String tag, MessageGroupBatch message) throws Exception {
            listeners.get(0).handler(tag, message);
        }

        @Override
        public void init(@NotNull ConnectionManager connectionManager, @NotNull MessageRouterConfiguration configuration) {

        }

        @Override
        public void init(@NotNull MessageRouterContext context) {

        }

        @Override
        public void send(MessageGroupBatch message) {

            messages.add(message);
        }

        @Override
        public void send(MessageGroupBatch message, String... queueAttr) {
            messages.add(message);
        }

        @Override
        public void sendAll(MessageGroupBatch message, String... queueAttr) {
            messages.add(message);
        }

        @Override
        public @Nullable SubscriberMonitor subscribe(MessageListener callback, String... queueAttr) {

            listeners.add(callback);

            return () -> {
            };
        }

        @Override
        public @Nullable SubscriberMonitor subscribeAll(MessageListener callback) {
            listeners.add(callback);

            return () -> {
            };
        }

        @Override
        public @Nullable SubscriberMonitor subscribeAll(MessageListener callback, String... queueAttr) {
            listeners.add(callback);

            return () -> {
            };
        }

        @Override
        public void close() {

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
        public void send(EventBatch message) {

        }

        @Override
        public void send(EventBatch message, String... queueAttr) {

        }

        @Override
        public void sendAll(EventBatch message, String... queueAttr) {

        }

        @Override
        public void close() {

        }
    }


}
