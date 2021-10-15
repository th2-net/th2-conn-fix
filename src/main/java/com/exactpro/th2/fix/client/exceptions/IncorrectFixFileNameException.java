package com.exactpro.th2.fix.client.exceptions;

public class IncorrectFixFileNameException extends Exception {

    public IncorrectFixFileNameException(String message) {
        super(message);
    }

    public IncorrectFixFileNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
