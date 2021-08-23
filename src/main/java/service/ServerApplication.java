package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.Application;
import quickfix.fix42.NewOrderSingle;

import java.util.HashMap;
import java.util.Map;

public class ServerApplication extends MessageCracker implements Application {

    private static final Logger log = LoggerFactory.getLogger(ServerApplication.class);
    private final Map<SessionID, Session> sessions = new HashMap<>();
    private SessionID sessionID;

    @Override
    public void onLogon(SessionID sessionId) {
        log.info(">> onLogon for session: {}", sessionId);

        Session session = Session.lookupSession(sessionId);
        if (session != null) {
            sessions.putIfAbsent(sessionId, session);
        } else {
            log.warn("Requested session is not found.");
        }
    }

    @Override
    public void onCreate(SessionID sessionId) {
        log.info(">> onCreate for session: {}", sessionId);
        this.sessionID = sessionId;
        Session session = Session.lookupSession(sessionId);
        if (session != null) {
            sessions.put(sessionId, session);
        } else {
            log.warn("Requested session is not found.");
        }
    }

    @Override
    public void onLogout(SessionID sessionId) {
        log.info(">> onLogout for session: {}", sessionId);
        sessions.remove(sessionId);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
        log.info(">> toAdmin for session: {} with message {}", sessionId, message);
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        log.info("<< fromAdmin for session: {} with message {}", sessionId, message);
    }

    @Override
    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
        log.info(">> toApp for session: {} with message {}", sessionId, message);
    }

    @Override
    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        crack(message, sessionID);
    }


    public void onMessage(NewOrderSingle message, SessionID sessionID) {
        System.out.println("From app: " + message + " session ID: " + sessionID);

    }

    public SessionID getSessionID() {
        return sessionID;
    }
    public Session getSession(){
        return Session.lookupSession(sessionID);
    }
}
