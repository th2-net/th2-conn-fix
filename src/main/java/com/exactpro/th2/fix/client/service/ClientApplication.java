package com.exactpro.th2.fix.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.Application;
import quickfix.Message;
import quickfix.SessionID;

public class ClientApplication implements Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientApplication.class);

    @Override
    public void onLogon(SessionID sessionId) {
        LOGGER.info(">> onLogon for session: {}", sessionId);
    }

    @Override
    public void onCreate(SessionID sessionId) {
        LOGGER.info(">> onCreate for session: {}", sessionId);
    }

    @Override
    public void onLogout(SessionID sessionId) {
        LOGGER.info(">> onLogout for session: {}", sessionId);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId) {
    }

    @Override
    public void toApp(Message message, SessionID sessionId) {
        LOGGER.info(">> toApp for session: {} with message {}", sessionId, message);
    }

    @Override
    public void fromApp(Message message, SessionID sessionID) {
        LOGGER.info("<< From app: " + message + " session ID: " + sessionID);
    }
}
