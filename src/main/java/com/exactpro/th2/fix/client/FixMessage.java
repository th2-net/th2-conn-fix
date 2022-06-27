package com.exactpro.th2.fix.client;

import com.exactpro.th2.common.grpc.EventID;
import org.apache.mina.util.ExpiringMap;
import quickfix.FieldMap;
import quickfix.Message;

public class FixMessage extends Message {

    public static final int DEFAULT_TIME_TO_LIVE_SECONDS = 30;
    private static final ExpiringMap<String, EventID> MESSAGE_PARENT_EVENT_IDS = new ExpiringMap<>(DEFAULT_TIME_TO_LIVE_SECONDS);

    protected final FixHeader header;
    protected final FixTrailer trailer;
    protected EventID parentEventID;
    protected Message message;

    static {
        MESSAGE_PARENT_EVENT_IDS.getExpirer().startExpiringIfNotStarted();
    }

    public FixMessage(int[] fieldOrderBody, int[] fieldOrderHeader, int[] fieldOrderTrailer) {
        super(fieldOrderBody);
        super.header = this.header = new FixHeader(fieldOrderHeader);
        super.trailer = this.trailer = new FixTrailer(fieldOrderTrailer);
    }

    public FixMessage(Message message, EventID parentEventID) {
        super(message.getFieldOrder());
        super.header = this.header = new FixHeader(message.getHeader().getFieldOrder());
        super.trailer = this.trailer = new FixTrailer(message.getTrailer().getFieldOrder());
        this.message = message;
        this.parentEventID = parentEventID;
    }

    @Override
    public Object clone() {
        FixMessage message = new FixMessage(getFieldOrder(), getHeader().getFieldOrder(), getTrailer().getFieldOrder());
        message.initializeFrom(this);
        message.getFixHeader().initializeFrom(getHeader());
        message.getFixTrailer().initializeFrom(getTrailer());
        message.message = this.message;
        message.parentEventID = this.parentEventID;
        return message;
    }

    public static EventID getMessageParentEventId(String message) {
        return MESSAGE_PARENT_EVENT_IDS.get(message);
    }

    @Override
    public String toString() {

        String res = super.toString();
        if (parentEventID != null && !parentEventID.getId().equals("")) {
            MESSAGE_PARENT_EVENT_IDS.put(res, parentEventID);
        }
        return res;
    }

    public FixHeader getFixHeader() {
        return header;
    }

    public FixTrailer getFixTrailer() {
        return trailer;
    }

    private static final class FixHeader extends Header {
        public FixHeader(int[] fieldOrder) {
            super(fieldOrder);
        }

        @Override
        protected void initializeFrom(FieldMap source) {
            super.initializeFrom(source);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    private static final class FixTrailer extends Trailer {
        public FixTrailer(int[] fieldOrder) {
            super(fieldOrder);
        }

        @Override
        protected void initializeFrom(FieldMap source) {
            super.initializeFrom(source);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}