package com.exactpro.th2.fix.client.util;

import com.exactpro.th2.common.event.Event;
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

    public static String rawToString(AnyMessage message) {
        return new String(message.getRawMessage().getBody().toByteArray(), StandardCharsets.UTF_8);
    }

    public static String getSessionAlias(AnyMessage message) {
        return message.getRawMessage().getMetadata().getId().getConnectionId().getSessionAlias();
    }

    public static String getParentEventID(AnyMessage message, String rootEventID) {
        return message.getRawMessage().getParentEventId().getId().isEmpty() ? rootEventID : message.getRawMessage().getParentEventId().getId();
    }

    public static Event getErrorEvent(String name) {
        return Event
                .start()
                .endTimestamp()
                .type("Error")
                .name(name)
                .status(Event.Status.FAILED);
    }

    public static Event getSuccessfulEvent(MessageGroupBatch message, String name) {

        return Event
                .start()
                .messageID(message
                        .getGroupsList()
                        .get(0)
                        .getMessagesList()
                        .get(0)
                        .getRawMessage()
                        .getMetadata()
                        .getId())
                .type("info")
                .name(name)
                .status(Event.Status.PASSED);
    }
}
