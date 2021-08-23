package util;

import com.exactpro.th2.common.grpc.*;
import com.exactpro.th2.common.message.MessageUtils;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;

import java.time.Instant;

public class MessageUtil {

    public static String toPrettyString(MessageOrBuilder messageOrBuilder) throws InvalidProtocolBufferException {
       return JsonFormat.printer().omittingInsignificantWhitespace().includingDefaultValueFields().print(messageOrBuilder);
    }


    public static MessageGroupBatch toBatch(byte[] byteArray, ConnectionID connectionID, Direction direction, long sequence){
        RawMessage.Builder rawMessage = RawMessage.newBuilder();
        rawMessage.setBody(ByteString.copyFrom(byteArray));

        RawMessageMetadata.Builder rawMessageMetadata = rawMessage.getMetadataBuilder();
        rawMessageMetadata.setTimestamp(MessageUtils.toTimestamp(Instant.now()));

        MessageID.Builder messageId = rawMessageMetadata.getIdBuilder();
        messageId.setConnectionId(connectionID);
        messageId.setDirection(direction);
        messageId.setSequence(sequence);

        AnyMessage.Builder anyMessage = AnyMessage.newBuilder().setRawMessage(rawMessage);
        MessageGroup.Builder messageGroup= MessageGroup.newBuilder().addMessages(anyMessage);
        MessageGroupBatch messageGroupBatch = MessageGroupBatch.newBuilder().addGroups(messageGroup).build();

        return messageGroupBatch;
    }

}
