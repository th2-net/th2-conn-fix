package com.exactpro.th2.fix.client;

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
import com.exactpro.th2.fix.client.exceptions.CreatingConfigFileException;
import com.exactpro.th2.fix.client.fixBean.FixBean;
import com.exactpro.th2.fix.client.util.MessageUtil;
import com.google.protobuf.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import quickfix.ConfigError;
import quickfix.Group;
import quickfix.IncorrectDataFormat;
import quickfix.Message;
import quickfix.field.ApplVerID;
import quickfix.field.BeginString;
import quickfix.field.HandlInst;
import quickfix.field.MsgType;
import quickfix.field.NoPartyIDs;
import quickfix.field.NoSides;
import quickfix.field.NoTradingSessions;
import quickfix.field.OrdType;
import quickfix.field.PartyID;
import quickfix.field.PartyIDSource;
import quickfix.field.PartyRole;
import quickfix.field.PreviouslyReported;
import quickfix.field.SenderCompID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TargetCompID;
import quickfix.field.TradeReportID;
import quickfix.field.TradingSessionID;
import quickfix.field.TransactTime;
import quickfix.field.TrdType;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;


public class MainTest extends Main {

    @Test //for manual test
    public void runTest() throws Exception {

//newOrderSingle
//        Message fixMessage = new Message();
//        Message.Header header = fixMessage.getHeader();
//        header.setField(new BeginString("FIX.4.2"));
//        header.setField(new MsgType("D"));
//        header.setField(new SenderCompID("client"));
//        header.setField(new TargetCompID("server"));
//        header.setField(new SenderSubID("sendSubId"));
//        header.setField(new TargetSubID("tarSubId"));

//
//        quickfix.fix42.NewOrderSingle fixMessage2 = new quickfix.fix42.NewOrderSingle(
//                new ClOrdID("ClOrdID"),
//                new HandlInst('3'),
//                new Symbol("Symbol"),
//                new Side('1'),
//                new TransactTime(LocalDateTime.now()),
//                new OrdType('1'));
//        fixMessage2.setField(new SenderCompID("client2"));
//        fixMessage2.setField(new TargetCompID("server"));

        Main.Settings settings = new Settings();

        FixBean fixBean = new FixBean();
        fixBean.setBeginString("FIX.4.4");
        fixBean.setSenderCompID("client");
        fixBean.setTargetCompID("server");
        fixBean.setSocketConnectPort(9877L);
        fixBean.setSessionAlias("client1");
        fixBean.setDataDictionary(Path.of("src/test/java/resources/FIX44.xml"));
        fixBean.setOrderingFields("true");
        fixBean.setStartTime("00:00:00 Europe/Moscow");
        fixBean.setEndTime("21:15:00 Europe/Moscow");
        fixBean.setStartDay("monday");
        fixBean.setEndDay("sunday");
        fixBean.setReconnectInterval(10L);
        fixBean.setAutorelogin(true);
        fixBean.setResetOnLogon("false");
        fixBean.setUseDefaultApplVerID(true);
        fixBean.setUsername("username");
        fixBean.setPassword("1234");
        fixBean.setNewPassword("123");
        fixBean.setCheckRequiredTags("true");
        fixBean.setSeqNumberFromRejectRegexp("Wrong sequence number!");
        fixBean.setSeqNumSender(2);
        fixBean.setSeqNumTarget(2);
        fixBean.setHeartBtInt(30L);
        fixBean.setFileLogPath("outgoing/");
        fixBean.setFileStorePath("storage/messages/");
        fixBean.setSocketConnectHost("localhost");


        FixBean fixBean1 = new FixBean();
        fixBean1.setBeginString("FIXT.1.1");
        fixBean1.setDefaultApplVerID("9");
        fixBean1.setSenderCompID("client2");
        fixBean1.setTargetCompID("server");
        fixBean1.setSocketConnectPort(9877L);
        fixBean1.setSessionAlias("client2");
        fixBean1.setTransportDataDictionary(Path.of("src/test/java/resources/FIXT11.xml"));
        fixBean1.setAppDataDictionary(Path.of("src/test/java/resources/FIX50SP2.xml"));
        fixBean1.setStartTime("15:15:00 Europe/Moscow");
        fixBean1.setEndTime("21:15:00 Europe/Moscow");
        fixBean1.setStartDay("monday");
        fixBean1.setEndDay("sunday");
        fixBean1.setAutorelogin(false);
        fixBean1.setResetOnLogon("false");
        fixBean1.setPassword("1234");
        fixBean1.setNewPassword("123");
        fixBean1.setResetOnLogon("true");
        fixBean.setHeartBtInt(30L);
        fixBean.setFileLogPath("outgoing/");
        fixBean.setFileStorePath("storage/messages/");
        fixBean.setSocketConnectHost("localhost");

        List<FixBean> fixBeans = new ArrayList<>();
        fixBeans.add(fixBean);
//        fixBeans.add(fixBean1);
        settings.setSessionSettings(fixBeans);

        System.out.println(settings);
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


        Message fixMessage2 = new Message();
        Message.Header header2 = fixMessage2.getHeader();
        header2.setField(new BeginString("FIXT.1.1"));
        header2.setString(ApplVerID.FIELD, "9");
        header2.setField(new MsgType("D"));
        header2.setField(new SenderCompID("client2"));
        header2.setField(new TargetCompID("server2"));
        fixMessage2.setString(HandlInst.FIELD, "1");
        fixMessage2.setString(Symbol.FIELD, "symbol");
        fixMessage2.setString(Side.FIELD, "1");
        fixMessage2.setUtcTimeStamp(TransactTime.FIELD, LocalDateTime.now());
        fixMessage2.setChar(OrdType.FIELD, '1');

        Group group = new Group(NoTradingSessions.FIELD, TradingSessionID.FIELD);

        group.setString(TradingSessionID.FIELD, "1");
        fixMessage2.addGroup(group);

        group.setString(TradingSessionID.FIELD, "2");
        fixMessage2.addGroup(group);

        group.setString(TradingSessionID.FIELD, "3");
        fixMessage2.addGroup(group);

        Message fixMessage1 = new Message();
        Message.Header headerClient1 = fixMessage1.getHeader();
        headerClient1.setField(new BeginString("FIXT.1.1"));
        headerClient1.setField(new MsgType(MsgType.TRADE_CAPTURE_REPORT));
        headerClient1.setField(new SenderCompID("client"));
        headerClient1.setField(new TargetCompID("server"));
        headerClient1.setString(ApplVerID.FIELD, "9");


        fixMessage1.setField(new TradeReportID("tradeID"));
        fixMessage1.setField(new PreviouslyReported(true));
        fixMessage1.setField(new TrdType(1));

        Group noSidesGr1 = new Group(new NoSides().getField(), new Side().getField());
        noSidesGr1.setField(new Side('1'));

        Group noSidesGr2 = new Group(new NoSides().getField(), new Side().getField());
        noSidesGr2.setField(new Side('2'));

        Group noPartyIDsGr1 = new Group(new NoPartyIDs().getField(), new PartyID().getField());
        noPartyIDsGr1.setField(new PartyID("party1"));
        noPartyIDsGr1.setField(new PartyIDSource('1'));
        noPartyIDsGr1.setField(new PartyRole(1));

        Group noPartyIDsGr2 = new Group(new NoPartyIDs().getField(), new PartyID().getField());
        noPartyIDsGr2.setField(new PartyID("party2"));
        noPartyIDsGr2.setField(new PartyIDSource('2'));
        noPartyIDsGr2.setField(new PartyRole(2));

        noSidesGr1.addGroup(noPartyIDsGr1);
        noSidesGr1.addGroup(noPartyIDsGr2);

        fixMessage1.addGroup(noSidesGr1);
        fixMessage1.addGroup(noSidesGr2);


        MessageGroupBatch messageGroupBatch = MessageGroupBatch.newBuilder()
                .addGroups(MessageGroup.newBuilder()
                        .addMessages(AnyMessage.newBuilder()
                                .setRawMessage(RawMessage.newBuilder()
                                        .setBody(ByteString
                                                .copyFrom(fixMessage1
                                                        .toString()
                                                        .getBytes()))
                                        .setMetadata(RawMessageMetadata
                                                .newBuilder()
                                                .setId(MessageID
                                                        .newBuilder()
                                                        .setConnectionId(ConnectionID
                                                                .newBuilder()
                                                                .setSessionAlias("client1")
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
                                                                .setSessionAlias("client2")
                                                                .build())
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();

        Thread.sleep(14000);
        messageRouter.sendToSubscriber("client1", messageGroupBatch);
//        messageRouter.sendToSubscriber("client2", messageGroupBatch2);

        Thread.sleep(1000 * 6);

        String testString;
        int countOfOrders = 0;
        int countOfResponses = 0;

        for (MessageGroupBatch message : messageRouter.messages) {
            testString = MessageUtil.rawToString(message.getGroupsList().get(0).getMessagesList().get(0));
            if (testString.contains("\00135=D") || testString.contains("\00135=AE")) {
                countOfOrders++;
            }
            if (testString.contains("\00135=8")) {
                countOfResponses++;
            }

        }
        System.out.println(countOfOrders);
        Assert.assertEquals(countOfOrders, countOfResponses);
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