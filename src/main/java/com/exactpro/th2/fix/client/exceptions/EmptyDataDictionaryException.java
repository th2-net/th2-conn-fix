package com.exactpro.th2.fix.client.exceptions;

public class EmptyDataDictionaryException extends Exception {
    public EmptyDataDictionaryException(String message) {
        super(message);
    }

    public EmptyDataDictionaryException(String message, Throwable cause) {
        super(message, cause);
    }
}
