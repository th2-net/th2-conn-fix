package com.exactpro.th2.fix.client.util;

import com.exactpro.th2.common.grpc.AnyMessage;
import com.exactpro.th2.common.grpc.ConnectionID;
import com.exactpro.th2.common.grpc.Direction;
import com.exactpro.th2.common.grpc.MessageGroup;
import com.exactpro.th2.common.grpc.MessageGroupBatch;
import com.exactpro.th2.common.grpc.MessageID;
import com.exactpro.th2.common.grpc.RawMessage;
import com.exactpro.th2.common.grpc.RawMessageMetadata;
import com.exactpro.th2.common.message.MessageUtils;
import com.google.protobuf.ByteString;
import quickfix.SessionID;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class MessageUtil {

    public static MessageGroupBatch toBatch(byte[] byteArray, ConnectionID connectionID, Direction direction, long sequence) {
        RawMessage.Builder rawMessage = RawMessage.newBuilder();
        rawMessage.setBody(ByteString.copyFrom(byteArray));

        RawMessageMetadata.Builder rawMessageMetadata = rawMessage.getMetadataBuilder();
        rawMessageMetadata.setTimestamp(MessageUtils.toTimestamp(Instant.now()));

        MessageID.Builder messageId = rawMessageMetadata.getIdBuilder();
        messageId.setConnectionId(connectionID);
        messageId.setDirection(direction);
        messageId.setSequence(sequence);

        AnyMessage.Builder anyMessage = AnyMessage.newBuilder().setRawMessage(rawMessage);
        MessageGroup.Builder messageGroup = MessageGroup.newBuilder().addMessages(anyMessage);
        MessageGroupBatch messageGroupBatch = MessageGroupBatch.newBuilder().addGroups(messageGroup).build();

        return messageGroupBatch;
    }

    public static SessionID getSessionID(String message) {

        String senderSubID = "";
        String senderLocationID = "";
        String targetSubID = "";
        String targetLocationID = "";
        String sessionQualifier = "";

        char soh = '\u0001'; //quickfix tags delimiter
        String subStringSenderCompId = soh + "49=";
        String subStringTargetCompId = soh + "56=";
        String subStringSenderSubId = soh + "50=";
        String subStringSenderLocationId = soh + "142=";
        String subStringTargetSubId = soh + "57=";
        String subStringTargetLocationId = soh + "143=";

        int indexOfSenderCompId = message.indexOf(subStringSenderCompId) + 4;
        int indexOfTargetCompId = message.indexOf(subStringTargetCompId) + 4;
        int indexOfSenderSubId = message.indexOf(subStringSenderSubId) + 4;
        int indexOfSenderLocationID = message.indexOf(subStringSenderLocationId) + 4;
        int indexOfTargetSubID = message.indexOf(subStringTargetSubId) + 4;
        int indexOfTargetLocationID = message.indexOf(subStringTargetLocationId) + 4;

        String beginString = message.substring(2, message.indexOf(soh));
        String senderCompId = message.substring(indexOfSenderCompId, message.indexOf(soh, indexOfSenderCompId));
        String targetCompId = message.substring(indexOfTargetCompId, message.indexOf(soh, indexOfTargetCompId));
        if (indexOfSenderSubId != 3)
            senderSubID = message.substring(indexOfSenderSubId, message.indexOf(message.indexOf(soh, indexOfSenderSubId)));
        if (indexOfSenderLocationID != 3)
            senderLocationID = message.substring(indexOfSenderLocationID, message.indexOf(message.indexOf(soh, indexOfSenderLocationID)));
        if (indexOfTargetSubID != 3)
            targetSubID = message.substring(indexOfTargetSubID, message.indexOf(message.indexOf(soh, indexOfTargetSubID)));
        if (indexOfTargetLocationID != 3)
            targetLocationID = message.substring(indexOfTargetLocationID, message.indexOf(message.indexOf(soh, indexOfTargetLocationID)));

        return new SessionID(beginString, senderCompId, senderSubID, senderLocationID, targetCompId, targetSubID, targetLocationID, sessionQualifier);
    }

    public static String rawToString(AnyMessage message) {
        return new String(message.getRawMessage().getBody().toByteArray(), StandardCharsets.UTF_8);
    }

}
