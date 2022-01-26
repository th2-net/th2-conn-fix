package com.exactpro.th2.fix.client.service;

import com.exactpro.th2.fix.client.Main;
import com.exactpro.th2.fix.client.fixBean.FixBean;
import org.apache.mina.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.Application;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageUtils;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.BeginSeqNo;
import quickfix.field.DefaultApplVerID;
import quickfix.field.EndSeqNo;
import quickfix.field.MsgType;
import quickfix.field.NewPassword;
import quickfix.field.Password;
import quickfix.field.Username;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Map;

public class ClientApplication implements Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientApplication.class);
    private static final String SETTING_VALUE_YES = "Y";

    private final Main.Settings settings;

    public ClientApplication(Main.Settings settings) {
        this.settings = settings;
    }

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

        String msgType = getMsgType(message);
        Map<String, SessionID> sessionIDBySessionAlias = settings.getSessionIDsByAliases();

        if (MsgType.LOGON.equals(msgType)) {

            for (FixBean sessionSettings : settings.getSessionSettings()) {

                if (MessageUtils.getSessionID(message).equals(sessionIDBySessionAlias.get(sessionSettings.getSessionAlias()))) {

                    if (SETTING_VALUE_YES.equals(sessionSettings.getEncryptPassword())) {
                        Session session = Session.lookupSession(sessionId);

                        try {
                            session.getStore().refresh();
                        } catch (IOException e) {
                            LOGGER.error("Failed to update session state while preparing Logon message", e);
                        }

                        String username = validateLogonFieldsNotNullOrEmpty(sessionSettings.getUsername(), sessionId);
                        String password = validateLogonFieldsNotNullOrEmpty(sessionSettings.getPassword(), sessionId);
                        String newPassword = validateLogonFieldsNotNullOrEmpty(sessionSettings.getNewPassword(), sessionId);
                        String keyFile = sessionSettings.getEncryptionKeyFilePath();

                        PublicKey publicKey = null;
                        String encryptedPassword = null;
                        String encryptedNewPassword = null;

                        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(keyFile))) {
                            publicKey = (PublicKey) inputStream.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            LOGGER.error("Failed to open file with public key for encrypting", e);
                        }

                        try {
                            Cipher cipher = Cipher.getInstance("RSA");
                            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

                            byte[] encryptedPasswordBytes = cipher.doFinal(password.getBytes());
                            encryptedPassword = new String(Base64.encodeBase64(encryptedPasswordBytes));

                            if (newPassword != null && !newPassword.isEmpty()) {
                                byte[] encryptedNewPasswordBytes = cipher.doFinal(newPassword.getBytes());
                                encryptedNewPassword = new String(Base64.encodeBase64(encryptedNewPasswordBytes));
                            }
                        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException |
                                IllegalBlockSizeException | BadPaddingException e) {
                            LOGGER.error("Failed to encrypt password", e);
                        }

                        if (username != null && !username.isEmpty()) {
                            message.getHeader().setString(Username.FIELD, username);
                        }
                        if (encryptedPassword != null) {
                            message.getHeader().setString(Password.FIELD, encryptedPassword);
                        }
                        if (encryptedNewPassword != null) {
                            message.getHeader().setString(NewPassword.FIELD, encryptedNewPassword);
                        }
                    }

                    if (!sessionSettings.isUseDefaultApplVerID()) {
                        message.removeField(DefaultApplVerID.FIELD);
                    }
                }
            }
        }
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound {
        Session session = Session.lookupSession(sessionId);

        String msgType = getMsgType(message);

        if (MsgType.RESEND_REQUEST.equals(msgType)) {
            for (FixBean sessionSettings : settings.getSessionSettings()) {
                if (sessionId.equals(MessageUtils.getSessionID(message)) && SETTING_VALUE_YES.equals(sessionSettings.getFakeResendRequest())) {

                    MessageFactory messageFactory = session.getMessageFactory();
                    String beginString = sessionId.getBeginString();

                    int beginSeqNo = message.getInt(BeginSeqNo.FIELD);
                    int endSeqNo = message.getInt(EndSeqNo.FIELD);

                    if (endSeqNo == 0) {
                        endSeqNo = session.getExpectedSenderNum() - 1; //session.getExpectedSenderNum() returns next expected sender sequence number
                    }
                    try {
                        for (int i = beginSeqNo; i <= endSeqNo; i++) {
                            session.getStore().refresh();               // reopen files // possible race condition ?
                            session.setNextSenderMsgSeqNum(i);
                            Message heartbeat = messageFactory.create(beginString, MsgType.HEARTBEAT);
                            session.send(heartbeat);
                        }
                    } catch (IOException e) {
                        LOGGER.error("Failed to fill gaps in message sequences using heartbeats for session {}", sessionId, e);
                    }
                }
            }
        }
    }

    @Override
    public void toApp(Message message, SessionID sessionId) {
        LOGGER.info(">> toApp for session: {} with message {}", sessionId, message);
    }

    @Override
    public void fromApp(Message message, SessionID sessionID) {
        LOGGER.info("<< From app: " + message + " session ID: " + sessionID);
    }

    private String getMsgType(Message message) {
        String msgType = null;
        try {
            msgType = message.getHeader().getString(MsgType.FIELD);
        } catch (FieldNotFound e) {
            LOGGER.error("No MsgType in message {}", message);
        }
        return msgType;
    }

    private String validateLogonFieldsNotNullOrEmpty(String field, SessionID sessionID) {
        if (field == null || field.isEmpty()) {
            LOGGER.error("Username or/and password is absent or empty for session {}", sessionID);
            return null;
        }
        return field;
    }
}
