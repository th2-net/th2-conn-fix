package com.exactpro.th2.fix.client.util;

import com.exactpro.th2.common.grpc.*;
import com.exactpro.th2.common.message.MessageUtils;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import quickfix.SessionID;

import java.time.Instant;

public class MessageUtil {

    public static String toPrettyString(MessageOrBuilder messageOrBuilder) throws InvalidProtocolBufferException {
        return JsonFormat.printer().omittingInsignificantWhitespace().includingDefaultValueFields().print(messageOrBuilder);
    }


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

        int indexOfSenderCompId = message.indexOf("\u000149=") + 4;
        int indexOfTargetCompId = message.indexOf("\u000156=") + 4;

        String substringFromSenderCompId = message.substring(indexOfSenderCompId);
        String substringFromTargetCompId = message.substring(indexOfTargetCompId);

        String beginString = message.substring(2, message.indexOf('\u0001'));
        String senderCompId = message.substring(indexOfSenderCompId, indexOfSenderCompId + substringFromSenderCompId.indexOf('\u0001'));
        String targetCompId = message.substring(indexOfTargetCompId, indexOfTargetCompId + substringFromTargetCompId.indexOf('\u0001'));

        return new SessionID(beginString, senderCompId, targetCompId);
    }
}
