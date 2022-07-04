package com.exactpro.th2.fix.client.fixBean;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.nio.file.Path;

import static com.exactpro.th2.fix.client.util.FixBeanUtil.addToConfig;
import static com.exactpro.th2.fix.client.util.FixBeanUtil.requireNotNullOrBlank;
import static quickfix.FileLogFactory.SETTING_FILE_LOG_PATH;
import static quickfix.FileLogFactory.SETTING_LOG_HEARTBEATS;
import static quickfix.FileStoreFactory.SETTING_FILE_STORE_PATH;
import static quickfix.Initiator.SETTING_RECONNECT_INTERVAL;
import static quickfix.Initiator.SETTING_SOCKET_CONNECT_HOST;
import static quickfix.Initiator.SETTING_SOCKET_CONNECT_PORT;
import static quickfix.Session.*;
import static quickfix.SessionSettings.BEGINSTRING;
import static quickfix.SessionSettings.SENDERCOMPID;
import static quickfix.SessionSettings.SENDERLOCID;
import static quickfix.SessionSettings.SENDERSUBID;
import static quickfix.SessionSettings.TARGETCOMPID;
import static quickfix.SessionSettings.TARGETLOCID;
import static quickfix.SessionSettings.TARGETSUBID;
import static quickfix.mina.ssl.SSLSupport.SETTING_CIPHER_SUITES;
import static quickfix.mina.ssl.SSLSupport.SETTING_ENABLED_PROTOCOLS;
import static quickfix.mina.ssl.SSLSupport.SETTING_KEY_STORE_NAME;
import static quickfix.mina.ssl.SSLSupport.SETTING_KEY_STORE_PWD;
import static quickfix.mina.ssl.SSLSupport.SETTING_USE_SSL;

public class FixBean extends BaseFixBean {


    private static final String LOGON_TAG_USERNAME = "553=";
    private static final String LOGON_TAG_PASSWORD = "554=";
    private static final String LOGON_TAG_NEW_PASSWORD = "925=";


    @JsonProperty(required = true)
    protected String beginString = "FIX.4.2";
    @JsonProperty(required = true)
    protected String socketConnectHost = "localhost";
    @JsonProperty(required = true)
    protected long socketConnectPort = 9877;
    @JsonProperty(required = true)
    protected String senderCompID = null;
    protected String senderSubID = null;
    protected String senderLocationID = null;
    protected String sessionAlias = null;
    protected String username = null;
    protected String password = null;
    protected String newPassword = null;
    protected String encryptionKeyFilePath = null;
    protected Integer seqNumSender = 0;
    protected Integer seqNumTarget = 0;

    private int logonTagIndex = 0;


    public StringBuilder toConfig(String sectionName) {
        StringBuilder stringBuilder = super.toConfig(sectionName);
        addToConfig(BEGINSTRING, beginString, stringBuilder);
        addToConfig(SETTING_SOCKET_CONNECT_HOST, socketConnectHost, stringBuilder);
        addToConfig(SETTING_SOCKET_CONNECT_PORT, socketConnectPort, stringBuilder);
        addToConfig(SENDERCOMPID, senderCompID, stringBuilder);
        addToConfig(SENDERSUBID, senderSubID, stringBuilder);
        addToConfig(SENDERLOCID, senderLocationID, stringBuilder);
        addToConfig(TARGETCOMPID, targetCompID, stringBuilder);
        addToConfig(TARGETSUBID, targetSubID, stringBuilder);
        addToConfig(TARGETLOCID, targetLocationID, stringBuilder);
        addToConfig(SETTING_DATA_DICTIONARY, dataDictionary, stringBuilder);
        addToConfig(SETTING_APP_DATA_DICTIONARY, appDataDictionary, stringBuilder);
        addToConfig(SETTING_TRANSPORT_DATA_DICTIONARY, transportDataDictionary, stringBuilder);
        addToConfig(SETTING_DEFAULT_APPL_VER_ID, defaultApplVerID, stringBuilder);
        addToConfig(SETTING_START_TIME, startTime, stringBuilder);
        addToConfig(SETTING_END_TIME, endTime, stringBuilder);
        addToConfig(SETTING_START_DAY, startDay,stringBuilder);
        addToConfig(SETTING_END_DAY, endDay, stringBuilder);
        addToConfig(getSettingLogonTag(username), LOGON_TAG_USERNAME + username, stringBuilder);
        addToConfig(getSettingLogonTag(password), LOGON_TAG_PASSWORD + password, stringBuilder);
        addToConfig(getSettingLogonTag(newPassword), LOGON_TAG_NEW_PASSWORD + newPassword, stringBuilder);
        addToConfig(SETTING_TIMESTAMP_PRECISION, timeStampPrecision, stringBuilder);
        addToConfig(SETTING_ENABLE_NEXT_EXPECTED_MSG_SEQ_NUM, enableNextExpectedMsgSeqNum, stringBuilder);
        addToConfig(SETTING_REQUIRES_ORIG_SENDING_TIME, requiresOrigSendingTime, stringBuilder);
        addToConfig(SETTING_TIMEZONE, timeZone, stringBuilder);
        addToConfig(SETTING_NON_STOP_SESSION, nonStopSession, stringBuilder);
        addToConfig(SETTING_FILE_STORE_PATH, fileStorePath, stringBuilder);
        addToConfig(SETTING_FILE_LOG_PATH, fileLogPath, stringBuilder);
        addToConfig(SETTING_RECONNECT_INTERVAL, reconnectInterval, stringBuilder);
        addToConfig(SETTING_HEARTBTINT, heartBtInt, stringBuilder);
        addToConfig(SETTING_VALIDATE_USER_DEFINED_FIELDS, validateUserDefinedFields, stringBuilder);
        addToConfig(SETTING_VALIDATE_INCOMING_MESSAGE, validateIncomingMessage, stringBuilder);
        addToConfig(SETTING_REFRESH_ON_LOGON, refreshOnLogon, stringBuilder);
        addToConfig(SETTING_RESET_ON_LOGON, resetOnLogon, stringBuilder);
        addToConfig(SETTING_RESET_ON_LOGOUT, resetOnLogout, stringBuilder);
        addToConfig(SETTING_RESET_ON_DISCONNECT, resetOnDisconnect, stringBuilder);
        addToConfig(SETTING_LOG_HEARTBEATS, logHeartBeats, stringBuilder);
        addToConfig(SETTING_CHECK_LATENCY, checkLatency, stringBuilder);
        addToConfig(SETTING_MAX_LATENCY, maxLatency, stringBuilder);
        addToConfig(SETTING_ALLOW_UNKNOWN_MSG_FIELDS, allowUnknownMsgFields, stringBuilder);
        addToConfig(SETTING_REJECT_INVALID_MESSAGE, rejectInvalidMessage, stringBuilder);
        addToConfig(SETTING_VALIDATE_FIELDS_OUT_OF_ORDER, validateFieldsOutOfOrder, stringBuilder);
        addToConfig(SETTING_VALIDATE_FIELDS_HAVE_VALUES, validateFieldsHaveValues, stringBuilder);
        addToConfig(SETTING_USE_SSL, socketUseSSL, stringBuilder);
        addToConfig(SETTING_KEY_STORE_NAME, socketKeyStore, stringBuilder);
        addToConfig(SETTING_KEY_STORE_PWD, socketKeyStorePassword, stringBuilder);
        addToConfig(SETTING_ENABLED_PROTOCOLS, enabledProtocols, stringBuilder);
        addToConfig(SETTING_CIPHER_SUITES, cipherSuites, stringBuilder);
        addToConfig(SETTING_VALIDATE_SEQUENCE_NUMBERS, validateSequenceNumbers, stringBuilder);
        addToConfig(SETTING_LOGON_TIMEOUT, logonTimeout, stringBuilder);
        addToConfig(SETTING_LOGOUT_TIMEOUT, logoutTimeout, stringBuilder);
        addToConfig(SETTING_USE_DATA_DICTIONARY, useDataDictionary, stringBuilder);
        addToConfig(SETTING_CHECK_REQUIRED_TAGS, checkRequiredTags, stringBuilder);
        return stringBuilder;
    }

    private String getSettingLogonTag(String value){
        if (value == null || value.isEmpty()){
            return null;
        }
        String logonTag = SETTING_LOGON_TAG;
        if (logonTagIndex != 0) {
            logonTag = SETTING_LOGON_TAG + logonTagIndex;
        }
        logonTagIndex++;
        return logonTag;
    }

    public void setSenderCompID(String senderCompID) {
        this.senderCompID = requireNotNullOrBlank(SENDERCOMPID, senderCompID);
    }

    public void setSessionAlias(String sessionAlias) {
        this.sessionAlias = requireNotNullOrBlank("SessionAlias", sessionAlias);
    }

    public void setSenderSubID(String senderSubID) {
        this.senderSubID = requireNotNullOrBlank(SENDERSUBID, senderSubID);
    }

    public void setSenderLocationID(String senderLocationID) {
        this.senderLocationID = requireNotNullOrBlank(SENDERLOCID, senderLocationID);
    }

    public void setUsername(String username) {
        this.username = requireNotNullOrBlank(SETTING_LOGON_TAG, username);
    }

    public void setPassword(String password) {
        this.password = requireNotNullOrBlank(SETTING_LOGON_TAG + 1, password);
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = requireNotNullOrBlank(SETTING_LOGON_TAG + 2, newPassword);
    }

    public String getEncryptionKeyFilePath() {
        return encryptionKeyFilePath;
    }

    public void setEncryptionKeyFilePath(String encryptionKeyFilePath) {
        this.encryptionKeyFilePath = requireNotNullOrBlank("EncryptionKeyFilePath", encryptionKeyFilePath);
    }

    public void setSeqNumSender(int seqNumSender) {
        if (seqNumSender < 0){
            throw new IllegalArgumentException("seqNumSender must not be negative");
        }
        this.seqNumSender = seqNumSender;
    }

    public void setSeqNumTarget(int seqNumTarget) {
        if (seqNumTarget < 0){
            throw new IllegalArgumentException("seqNumTarget must not be negative");
        }
        this.seqNumTarget = seqNumTarget;
    }

    @Override
    public String getBeginString() {
        return beginString;
    }

    @Override
    public void setBeginString(String beginString) {
        this.beginString = beginString;
    }

    @Override
    public String getSocketConnectHost() {
        return socketConnectHost;
    }

    @Override
    public void setSocketConnectHost(String socketConnectHost) {
        this.socketConnectHost = socketConnectHost;
    }

    @Override
    public Long getSocketConnectPort() {
        return socketConnectPort;
    }

    public void setSocketConnectPort(long socketConnectPort) {
        this.socketConnectPort = socketConnectPort;
    }

    public Integer getSeqNumTarget() {
        return seqNumTarget;
    }

    public Integer getSeqNumSender() {
        return seqNumSender;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getSessionAlias() {
        return sessionAlias;
    }

    public String getSenderSubID() {
        return senderSubID;
    }

    public String getSenderLocationID() {
        return senderLocationID;
    }

    public String getSenderCompID() {
        return senderCompID;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append(SENDERCOMPID, senderCompID)
                .append(SENDERSUBID, senderSubID)
                .append(SENDERLOCID, senderLocationID)
                .append("SessionAlias", sessionAlias)
                .append(SETTING_LOGON_TAG, username)
                .append(SETTING_LOGON_TAG + 1, password)
                .append(SETTING_LOGON_TAG + 2, newPassword)
                .append("EncryptionKeyFilePath", encryptionKeyFilePath)
                .toString();
    }
}