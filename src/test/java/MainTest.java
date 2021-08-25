import com.exactpro.th2.common.grpc.*;
import com.exactpro.th2.common.schema.grpc.router.GrpcRouter;
import com.exactpro.th2.common.schema.message.MessageListener;
import com.exactpro.th2.common.schema.message.MessageRouter;
import com.exactpro.th2.common.schema.message.MessageRouterContext;
import com.exactpro.th2.common.schema.message.SubscriberMonitor;
import com.exactpro.th2.common.schema.message.configuration.MessageRouterConfiguration;
import com.exactpro.th2.common.schema.message.impl.rabbitmq.connection.ConnectionManager;
import com.exactpro.th2.fix.client.Main;
import com.exactpro.th2.fix.client.fixBean.FixBean;
import com.exactpro.th2.fix.client.util.MessageUtil;
import com.google.protobuf.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import quickfix.ConfigError;
import quickfix.Message;
import quickfix.field.BeginString;
import quickfix.field.MsgType;
import quickfix.field.SenderCompID;
import quickfix.field.TargetCompID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class MainTest extends Main{


    @Test
    public void getSessionTest(){
        String message = "8=FIX.4.2\u00019=105\u000135=8\u000134=1266\u000143=Y\u000149=server\u000152=20210824-08:00:11.858\u000156=client\u0001122=20210823-10:36:47.517\u000120=0\u000139=0\u0001150=0\u000110=112\u0001";
        Assert.assertEquals("FIX.4.2:server->client", MessageUtil.getSessionID(message).toString());
    }


    @Test
    public void runTest() throws Exception {

        Main.Settings settings = new Main.Settings();

        FixBean fixBean = new FixBean();
        FixBean fixBean1 = new FixBean();

        fixBean1.setSenderCompID(new SenderCompID("client2"));
        fixBean1.setSessionAlias("FIX.4.2:client2->server");
        fixBean1.setSocketConnectPort(9878);

        List<FixBean> fixBeans = new ArrayList<>();
        fixBeans.add(fixBean);
        fixBeans.add(fixBean1);
        settings.setFixBeanList(fixBeans);

        MyMessageRouter messageRouter = new MyMessageRouter();

        MessageRouter<EventBatch> eventRouter = new MyEventRouter();
        GrpcRouter grpcRouter = Mockito.mock(GrpcRouter.class);
        ConcurrentLinkedDeque<Resources> resources = new ConcurrentLinkedDeque<>();

        Thread thread = new Thread(() -> {
            try {
                Main.run(settings, messageRouter, eventRouter, grpcRouter, resources);
            } catch (ConfigError configError) {
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


//        NewOrderSingle fixMessage = new NewOrderSingle(
//                new ClOrdID("1"),
//                new HandlInst('1'),
//                new Symbol("ClientXXXMMMMMMM"),
//                new Side('1'),
//                new TransactTime(LocalDateTime.now()),
//                new OrdType('1'));
//        // market order fields
//        fixMessage.set(new OrderQty(1));
//        fixMessage.set(new Price(10.0));

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

        Thread.sleep(14000);

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

        List<MessageListener> listOfListeners = new ArrayList<>();
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
        public void send(MessageGroupBatch message, String... queueAttr){
            listOfMessages.add(message);
        }

        @Override
        public void sendAll(MessageGroupBatch message, String... queueAttr){
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
        public void close(){

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
        public void send(EventBatch message){

        }

        @Override
        public void send(EventBatch message, String... queueAttr){

        }

        @Override
        public void sendAll(EventBatch message, String... queueAttr){

        }

        @Override
        public void close(){

        }
    }


}
