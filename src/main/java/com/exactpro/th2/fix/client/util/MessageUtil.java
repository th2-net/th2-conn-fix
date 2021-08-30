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

        String sessionQualifier = "";
        return new SessionID(message.substring(2, message.indexOf('\u0001')),
                getTagValue(message, 49), getTagValue(message, 50),
                getTagValue(message, 142), getTagValue(message, 56),
                getTagValue(message, 57), getTagValue(message, 143), sessionQualifier);
    }

    private static String getTagValue(String message, int tagValue) {

        char soh = '\u0001'; //quickfix tags delimiter
        String result;
        try {
            String subStringId = soh + "" + tagValue + "=";
            int indexOfSubString = message.indexOf(subStringId) + subStringId.length();
            if (indexOfSubString == subStringId.length() - 1) return null;
            result = message.substring(indexOfSubString, message.indexOf(soh, indexOfSubString));
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    public static String rawToString(AnyMessage message) {
        return new String(message.getRawMessage().getBody().toByteArray(), StandardCharsets.UTF_8);
    }

}
