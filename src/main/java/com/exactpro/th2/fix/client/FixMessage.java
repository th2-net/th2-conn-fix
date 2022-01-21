package com.exactpro.th2.fix.client;

import quickfix.FieldMap;
import quickfix.Message;

public class FixMessage extends Message {

    protected final FixHeader header;
    protected final FixTrailer trailer;

    public FixMessage(int[] fieldOrderBody, int[] fieldOrderHeader, int[] fieldOrderTrailer) {
        super(fieldOrderBody);
        super.header = this.header = new FixHeader(fieldOrderHeader);
        super.trailer = this.trailer = new FixTrailer(fieldOrderTrailer);

    }

    @Override
    public Object clone() {
        FixMessage message = new FixMessage(getFieldOrder(), getHeader().getFieldOrder(), getTrailer().getFieldOrder());
        message.initializeFrom(this);
        message.getFixHeader().initializeFrom(getHeader());
        message.getFixTrailer().initializeFrom(getTrailer());
        return message;
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
    }

    private final class FixTrailer extends Trailer{
        public FixTrailer(int[] fieldOrder) {
            super(fieldOrder);
        }

        @Override
        protected void initializeFrom(FieldMap source) {
            super.initializeFrom(source);
        }
    }

}
