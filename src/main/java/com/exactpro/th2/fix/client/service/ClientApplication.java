package com.exactpro.th2.fix.client.service;

import com.exactpro.th2.fix.client.Main;
import com.exactpro.th2.fix.client.fixBean.FixBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.mina.util.Base64;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import quickfix.field.DefaultCstmApplVerID;
import quickfix.field.EndSeqNo;
import quickfix.field.MsgSeqNum;
import quickfix.field.MsgType;
import quickfix.field.NewPassword;
import quickfix.field.Password;
import quickfix.field.Text;
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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ClientApplication implements Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientApplication.class);
    private static final String SETTING_VALUE_YES = "Y";
    public static final String SEQUENCE = "sequence";

    private final Main.Settings settings;
    private final Map<SessionID, Integer> seqNumSender = new HashMap<>();
    private final Map<SessionID, Integer> seqNumTarget = new HashMap<>();
    private final Map<SessionID, Pattern> seqNumberRejectPattern = new HashMap<>();
    private final Map<SessionID, Pattern> seqNumberLogoutPattern = new HashMap<>();
    private boolean incorrectSenderMsgSeqNum = false;
    private boolean incorrectTargetMsgSeqNum = false;

    private Map<String, SessionID> sessionIDsByAliases;

    public ClientApplication(Main.Settings settings) {
        this.settings = settings;
        this.sessionIDsByAliases = settings.getSessionIDsByAliases();

        for (FixBean sessionSettings : settings.getSessionSettings()) {

            SessionID sessionID = sessionIDsByAliases.get(sessionSettings.getSessionAlias());

            Integer numSender = sessionSettings.getSeqNumSender();
            if (numSender != null && numSender != 0) {
                seqNumSender.put(sessionID, numSender);
            }

            Integer numTarget = sessionSettings.getSeqNumTarget();
            if (numTarget != null && numTarget != 0) {
                seqNumTarget.put(sessionID, numTarget);
            }

            seqNumberRejectPattern.put(sessionID, compilePattern(sessionSettings.getSeqNumberFromRejectRegexp()));
            seqNumberLogoutPattern.put(sessionID, compilePattern(sessionSettings.getSeqNumberFromLogoutRegexp()));
        }
    }

    @Nullable
    private Pattern compilePattern(String regexp) {
        try {
            if (regexp != null) {
                return Pattern.compile(regexp);
            }
        } catch (PatternSyntaxException e) {
            LOGGER.error("Wrong regexp " + regexp + " for setting", e);
        }
        return null;
    }

    @Override
    public void onLogon(SessionID sessionId) {
        LOGGER.info(">> onLogon for session: {}", sessionId);

    }

    @Override
    public void onCreate(SessionID sessionId) {

        Session session = Session.lookupSession(sessionId);

        Integer seqNumSender = this.seqNumSender.get(sessionId);
        try {
            if (seqNumSender != null) {
                session.setNextSenderMsgSeqNum(seqNumSender);
                LOGGER.info("Set next sender message sequence number: " + seqNumSender);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to set up next sender message sequence number: " + seqNumSender + " for the session: " + sessionId);
        }

        Integer seqNumTarget = this.seqNumTarget.get(sessionId);
        try {
            if (seqNumTarget != null) {
                session.setNextTargetMsgSeqNum(seqNumTarget);
                LOGGER.info("Set next target message sequence number: " + seqNumTarget);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to set up next target message sequence number: " + seqNumTarget + " for the session: " + sessionId);
        }


        LOGGER.info(">> onCreate for session: {}", sessionId);
    }

    @Override
    public void onLogout(SessionID sessionId) {
        LOGGER.info(">> onLogout for session: {}", sessionId);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {

        Session session = Session.lookupSession(sessionId);

        String msgType = getMsgType(message);
        Map<String, SessionID> sessionIDBySessionAlias = settings.getSessionIDsByAliases();

        if (MsgType.LOGON.equals(msgType)) {

            for (FixBean sessionSettings : settings.getSessionSettings()) {

                if (sessionId.equals(sessionIDBySessionAlias.get(sessionSettings.getSessionAlias()))) {

                    if (SETTING_VALUE_YES.equals(sessionSettings.getEncryptPassword())) {

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
                        LOGGER.debug("Username = {}, Password = {}, NewPassword = {}", username, password, newPassword);
                    }

                    String defaultCstmApplVerID = sessionSettings.getDefaultCstmApplVerID();

                    if (defaultCstmApplVerID != null) {
                        message.setString(DefaultCstmApplVerID.FIELD, defaultCstmApplVerID);
                    }

                    if (!sessionSettings.isUseDefaultApplVerID()) {
                        message.removeField(DefaultApplVerID.FIELD);
                    }
                }
            }

            if (incorrectSenderMsgSeqNum) {
                Integer seqNumSender = this.seqNumSender.get(sessionId);

                message.getHeader().setInt(MsgSeqNum.FIELD, seqNumSender);

                try {
                    LOGGER.info("set next sender MsgSeqNum after logout to: {}", seqNumSender);
                    session.getStore().refresh(); // reopen files // possible race condition ?
                    session.setNextSenderMsgSeqNum(seqNumSender);
                } catch (IOException e) {
                    LOGGER.error(" Failed to set next sender sequence number to {} for session: {}", seqNumSender, sessionId, e);
                }
                this.incorrectSenderMsgSeqNum = false;
            }

            if (incorrectTargetMsgSeqNum) {
                Integer seqNumTarget = this.seqNumTarget.get(sessionId);
                try {
                    LOGGER.info("set next target MsgSeqNum after logout to: {}", seqNumTarget);
                    session.getStore().refresh(); // reopen files // possible race condition ?
                    session.setNextTargetMsgSeqNum(seqNumTarget);
                } catch (IOException e) {
                    LOGGER.error("Failed to set next target sequence number to {} for session: {}", seqNumTarget, sessionId, e);
                }
                this.incorrectTargetMsgSeqNum = false;
            }
        }
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound {
        Session session = Session.lookupSession(sessionId);

        String msgType = getMsgType(message);

        if (MsgType.RESEND_REQUEST.equals(msgType)) {
            for (FixBean sessionSettings : settings.getSessionSettings()) {
                if (sessionId.equals(MessageUtils.getReverseSessionID(message)) && SETTING_VALUE_YES.equals(sessionSettings.getFakeResendRequest())) {

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

        if (MsgType.REJECT.equals(msgType)) {
            if (message.isSetField(Text.FIELD)) {
                String rejectText = message.getString(Text.FIELD);
                if (!extractSeqNumRejectTextRegexp(session, sessionId, rejectText)) {
                    extractSeqNumRejectTextPattern(session, sessionId, rejectText);
                }
            }
        }

        if (MsgType.LOGOUT.equals(msgType)) {
            if (message.isSetField(Text.FIELD)) {
                LOGGER.info("Logout received - {}", message);

                String text = message.getString(Text.FIELD);
                try {
                    if (!extractSeqNumLogoutTextRegexp(message, sessionId, text)) {
                        extractSeqNumLogoutTextPattern(message, sessionId, text);
                    }
                } catch (NumberFormatException e) {
                    LOGGER.error("Failed to update sender / target sequence numbers via text \"{}\" from logout message ", text, e);
                }
            }
        }

    }

    private void extractSeqNumLogoutTextPattern(Message message, SessionID sessionID, String text) throws FieldNotFound {
        int sendNum = -1;
        int targSeq = -1;
        if (containsAll(text, "MsgSeqNum", "too low, expecting")
                || containsAll(text, "Wrong sequence number!", "Too small to recover. Received: ", "Expected: ", ">.")
                || containsAll(text, "Sequence Number", "<", "expected")
                || containsAll(text, "MsgSeqNum", "less than expected")) {
            incorrectSenderMsgSeqNum = true;
            // extract 4 from the text: MsgSeqNum too low, expecting 4 but received 1
            sendNum = extractSeqNum(text);
            // DG: experimentally checked
            // only here set next seq num as sendNum-1.
            //sendNum = sendNum-1; // nikolay.antonov : It is seems doesn't works.
            targSeq = message.getHeader().getInt(MsgSeqNum.FIELD);
        } else if (text.startsWith("Error ! Expecting : ")) {
            incorrectTargetMsgSeqNum = true;
            // extract 1282 from the text: Error ! Expecting : 1282 but received : 1281
            sendNum = Integer.parseInt(text.split(" ")[4]);
            targSeq = message.getHeader().getInt(MsgSeqNum.FIELD);
        } else if (text.startsWith("Negative gap for the user")) {
            incorrectSenderMsgSeqNum = true;
            String num = text.substring(
                    text.lastIndexOf('[') + 1,
                    text.lastIndexOf(']'));
            sendNum = Integer.parseInt(num);

            //incorrectTargetMsgSeqNum = true; // TODO
            targSeq = message.getHeader().getInt(MsgSeqNum.FIELD); // TODO: +1 ?
        }
        if (sendNum != -1) {
            this.seqNumSender.put(sessionID, sendNum);
        }

        if (targSeq != -1) {
            this.seqNumTarget.put(sessionID, targSeq);
        }
    }

    public static int extractSeqNum(String text) {
        String value = StringUtils.substringBetween(text, "expecting ", " but received");
        value = value == null ? StringUtils.substringBetween(text, "Expected: ", ">") : value;
        value = value == null ? StringUtils.substringBetween(text, "expected (", ")") : value;
        value = value == null ? StringUtils.substringAfterLast(text, "less than expected").trim() : value;
        return Integer.parseInt(value);
    }

    private boolean extractSeqNumLogoutTextRegexp(Message message, SessionID sessionID, String text) throws FieldNotFound {

        Pattern seqNumberLogoutPattern = this.seqNumberLogoutPattern.get(sessionID);

        String expectedSeqNum = null;
        if (seqNumberLogoutPattern != null) {
            expectedSeqNum = extractSeqNumRegexp(text, seqNumberLogoutPattern);
            if (expectedSeqNum != null) {
                incorrectTargetMsgSeqNum = true;
                this.seqNumSender.put(sessionID, Integer.parseInt(expectedSeqNum));
                this.seqNumTarget.put(sessionID, message.getHeader().getInt(MsgSeqNum.FIELD));
            }
        }
        return expectedSeqNum != null;
    }

    private void extractSeqNumRejectTextPattern(Session session, SessionID sessionID, @NotNull String rejectText) {
        String expectedSeqNum;
        if (rejectText.startsWith("Wrong sequence number")) {
            if (!containsAll(rejectText, "Received:", "Expected:")) {
                LOGGER.info("Trying to change next sender message sequence number, but Text (58 tag) have unsupported format. {}", rejectText);
                return;
            }
            expectedSeqNum = rejectText.split("Expected:")[1].replaceAll("[\\D]", "");
            setSenderSeqNum(session, sessionID, rejectText, expectedSeqNum);
        }
    }

    public static boolean containsAll(String sentence, String... keywords) {
        for (String keyword : keywords) {
            if (!sentence.contains(keyword)) {
                return false;
            }
        }
        return true;
    }

    private boolean extractSeqNumRejectTextRegexp(Session session, SessionID sessionID, String rejectText) {

        Pattern seqNumberRejectPattern = this.seqNumberRejectPattern.get(sessionID);
        String expectedSeqNum = null;

        if (seqNumberRejectPattern != null) {
            try {
                expectedSeqNum = extractSeqNumRegexp(rejectText, seqNumberRejectPattern);
                if (expectedSeqNum != null) { //todo add events
                    setSenderSeqNum(session, sessionID, rejectText, expectedSeqNum);
                }
            } catch (Exception e) {
                LOGGER.error("Failed group {} in pattern {}", SEQUENCE, seqNumberRejectPattern);
            }
        }
        return expectedSeqNum != null;
    }

    private void setSenderSeqNum(Session session, SessionID sessionID, String rejectText, String expectedSeqNum) {
        try {
            int seqNumSender = Integer.parseInt(expectedSeqNum);
            this.seqNumSender.put(sessionID, seqNumSender);
            session.setNextSenderMsgSeqNum(seqNumSender);
            LOGGER.info("Set next sender message sequence number = {} for session: {}", seqNumSender, sessionID);
        } catch (NumberFormatException | IOException e) {
            LOGGER.error("Failed to update sender sequence number via text \"" + rejectText + "\" from reject message", e);
        }
    }

    public static String extractSeqNumRegexp(String text, @NotNull Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        String value = null;
        if (matcher.find()) {
            value = matcher.group(SEQUENCE);
            if (value == null) {
                LOGGER.error("Group \"{}\" is not in the pattern {}", SEQUENCE, pattern);
            }
        }
        if (value == null) {
            LOGGER.debug("Text: {}. Does not match the pattern: {}", text, pattern);
        }
        return value;
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
