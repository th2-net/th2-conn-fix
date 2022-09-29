package com.exactpro.th2.fix.client.util;

import com.exactpro.th2.common.grpc.AnyMessage;
import com.exactpro.th2.common.grpc.ConnectionID;
import com.exactpro.th2.common.grpc.Direction;
import com.exactpro.th2.common.grpc.MessageID;
import com.exactpro.th2.common.grpc.RawMessage;
import com.exactpro.th2.common.grpc.RawMessageMetadata;
import com.exactpro.th2.common.message.MessageUtils;
import com.google.protobuf.UnsafeByteOperations;

import java.time.Instant;

public class MessageUtil {

    public static RawMessage toRawMessage(byte[] byteArray, ConnectionID connectionID, Direction direction, long sequence) {
        RawMessage.Builder rawMessage = RawMessage.newBuilder();
        rawMessage.setBody(UnsafeByteOperations.unsafeWrap(byteArray));

        RawMessageMetadata.Builder rawMessageMetadata = rawMessage.getMetadataBuilder();
        rawMessageMetadata.setTimestamp(MessageUtils.toTimestamp(Instant.now()));

        MessageID.Builder messageId = rawMessageMetadata.getIdBuilder();
        messageId.setConnectionId(connectionID);
        messageId.setDirection(direction);
        messageId.setSequence(sequence);

        return rawMessage.build();
    }

    public static String rawToString(AnyMessage message) {
        return message.getRawMessage().getBody().toStringUtf8();
    }

    public static String getSessionAlias(AnyMessage message) {
        return message.getRawMessage().getMetadata().getId().getConnectionId().getSessionAlias();
    }

}