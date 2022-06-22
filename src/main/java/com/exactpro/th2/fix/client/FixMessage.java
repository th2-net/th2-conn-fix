package com.exactpro.th2.fix.client;

import com.exactpro.th2.common.grpc.EventID;
import org.apache.mina.util.ExpiringMap;
import quickfix.FieldMap;
import quickfix.Message;

public class FixMessage extends Message {

    public static final int DEFAULT_TIME_TO_LIVE = 30;

    protected final FixHeader header;
    protected final FixTrailer trailer;
    protected EventID parentEventID;
    public static ExpiringMap<String, EventID> parentEventIDs = new ExpiringMap<>(DEFAULT_TIME_TO_LIVE);

    public FixMessage(int[] fieldOrderBody, int[] fieldOrderHeader, int[] fieldOrderTrailer) {
        super(fieldOrderBody);
        super.header = this.header = new FixHeader(fieldOrderHeader);
        super.trailer = this.trailer = new FixTrailer(fieldOrderTrailer);
        parentEventIDs.getExpirer().startExpiringIfNotStarted();
    }

    public FixMessage(Message message, EventID parentEventID) {
        super(message.getFieldOrder());
        super.header = this.header = new FixHeader(message.getHeader().getFieldOrder());
        super.trailer = this.trailer = new FixTrailer(message.getTrailer().getFieldOrder());
        initializeFrom(message);
        this.header.initializeFrom(message.getHeader());
        this.trailer.initializeFrom(message.getTrailer());
        this.parentEventID = parentEventID;
        parentEventIDs.getExpirer().startExpiringIfNotStarted();
    }

    @Override
    public Object clone() {
        FixMessage message = new FixMessage(getFieldOrder(), getHeader().getFieldOrder(), getTrailer().getFieldOrder());
        message.initializeFrom(this);
        message.getFixHeader().initializeFrom(getHeader());
        message.getFixTrailer().initializeFrom(getTrailer());
        return message;
    }

    public EventID getParentEventID() {
        return parentEventID;
    }

    public void setParentEventID(EventID parentEventID) {
        this.parentEventID = parentEventID;
    }

    public static ExpiringMap<String, EventID> getParentEventIDs() {
        return parentEventIDs;
    }

    public static void setParentEventIDs(ExpiringMap<String, EventID> parentEventIDs) {
        FixMessage.parentEventIDs = parentEventIDs;
    }

    @Override
    public String toString() {

        String res = super.toString();
        parentEventIDs.put(res, parentEventID);
        return res;
    }

    public FixHeader getFixHeader() {
        return header;
    }

    public FixTrailer getFixTrailer() {
        return trailer;
    }

    private final class FixHeader extends Header{
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

    private final class FixTrailer extends Trailer{
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
