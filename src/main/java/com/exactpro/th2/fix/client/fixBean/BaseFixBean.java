package com.exactpro.th2.fix.client.fixBean;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.nio.file.Path;

import static com.exactpro.th2.fix.client.util.FixBeanUtil.addToConfig;
import static com.exactpro.th2.fix.client.util.FixBeanUtil.requireNotNullOrBlank;
import static com.exactpro.th2.fix.client.util.FixBeanUtil.convertFromBoolToYOrN;
import static quickfix.Session.*;
import static quickfix.SessionSettings.BEGINSTRING;
import static quickfix.SessionSettings.TARGETCOMPID;
import static quickfix.SessionSettings.TARGETLOCID;
import static quickfix.SessionSettings.TARGETSUBID;
import static quickfix.mina.ssl.SSLSupport.*;
import static quickfix.FileLogFactory.*;
import static quickfix.FileStoreFactory.*;
import static quickfix.SessionFactory.*;
import static quickfix.Initiator.*;

public class BaseFixBean {

    protected String fileStorePath = null;
    protected String fileLogPath = null;
    @JsonIgnore
    protected String connectionType = "initiator";
    protected Long reconnectInterval = null;
    protected Long heartBtInt = null;
    @JsonIgnore
    protected String useDataDictionary = null;
    protected String validateUserDefinedFields = null;
    protected String validateIncomingMessage = null;
    protected String refreshOnLogon = null;
    protected String nonStopSession = null;
    protected String resetOnLogon = null;
    protected String resetOnLogout = null;
    protected String resetOnDisconnect = null;
    protected String logHeartBeats = null;
    protected String checkLatency = null;
    protected Long maxLatency = null;
    protected String allowUnknownMsgFields = null;
    protected String rejectInvalidMessage = null;
    protected String validateFieldsOutOfOrder = null;
    protected String validateFieldsHaveValues = null;
    protected String socketUseSSL = null;
    protected String socketKeyStore = null;
    protected String socketKeyStorePassword = null;
    protected String enabledProtocols = null;
    protected String cipherSuites = null;
    protected String validateSequenceNumbers = null;
    protected Long logonTimeout = null;
    protected Long logoutTimeout = null;
    protected String requiresOrigSendingTime = null;
    protected String timeZone = null;
    protected String startTime = null;
    protected String endTime = null;
    protected String startDay = null;
    protected String endDay = null;
    protected String timeStampPrecision = null;
    protected String enableNextExpectedMsgSeqNum = null;
    protected String fakeResendRequest = null;
    protected String orderingFields = null;
    protected boolean autorelogin = true;
    protected boolean useDefaultApplVerID = true;
    protected String defaultCstmApplVerID = null;
    protected String persistMessages = null;
    protected String validateFieldsOutOfRange = "Y";
    protected String duplicateTagsAllowed = "N";
    protected String ignoreAbsenceOf141tag = "N";
    protected String checkRequiredTags = null;
    protected String seqNumberFromRejectRegexp = null;
    protected String seqNumberFromLogoutRegexp = null;
    protected String beginString = null;
    protected String socketConnectHost = null;
    protected Long socketConnectPort = null;
    protected String targetCompID = null;
    protected String targetSubID = null;
    protected String targetLocationID = null;
    protected String defaultApplVerID = null;
    protected String encryptPassword = null;
    protected Path dataDictionary = null;
    protected Path appDataDictionary = null;
    protected Path transportDataDictionary = null;
    public BaseFixBean() {
    }

    public StringBuilder toConfig(String sectionName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[").append(sectionName).append("]").append(System.lineSeparator());
        addToConfig(SETTING_FILE_STORE_PATH, fileStorePath, stringBuilder);
        addToConfig(SETTING_FILE_LOG_PATH, fileLogPath, stringBuilder);
        addToConfig(SETTING_CONNECTION_TYPE, connectionType, stringBuilder);
        addToConfig(SETTING_RECONNECT_INTERVAL, reconnectInterval, stringBuilder);
        addToConfig(SETTING_NON_STOP_SESSION, nonStopSession, stringBuilder);
        addToConfig(SETTING_HEARTBTINT, heartBtInt, stringBuilder);
        addToConfig(SETTING_USE_DATA_DICTIONARY, useDataDictionary, stringBuilder);
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
        addToConfig(SETTING_REQUIRES_ORIG_SENDING_TIME, requiresOrigSendingTime, stringBuilder);
        addToConfig(SETTING_TIMEZONE, timeZone, stringBuilder);
        addToConfig(SETTING_START_TIME, startTime, stringBuilder);
        addToConfig(SETTING_END_TIME, endTime, stringBuilder);
        addToConfig(SETTING_START_DAY, startDay, stringBuilder);
        addToConfig(SETTING_END_DAY, endDay, stringBuilder);
        addToConfig(SETTING_TIMESTAMP_PRECISION, timeStampPrecision, stringBuilder);
        addToConfig(SETTING_ENABLE_NEXT_EXPECTED_MSG_SEQ_NUM, enableNextExpectedMsgSeqNum, stringBuilder);
        addToConfig(SETTING_CHECK_REQUIRED_TAGS, checkRequiredTags, stringBuilder);
        addToConfig(SETTING_PERSIST_MESSAGES, persistMessages, stringBuilder);
        addToConfig(SETTING_DUPLICATE_TAGS_ALLOWED, duplicateTagsAllowed, stringBuilder);
        addToConfig(SETTING_IGNORE_ABSENCE_OF_141_TAG, ignoreAbsenceOf141tag, stringBuilder);
        addToConfig(BEGINSTRING, beginString, stringBuilder);
        addToConfig(SETTING_SOCKET_CONNECT_HOST, socketConnectHost, stringBuilder);
        addToConfig(SETTING_SOCKET_CONNECT_PORT, socketConnectPort, stringBuilder);
        addToConfig(TARGETCOMPID, targetCompID, stringBuilder);
        addToConfig(TARGETSUBID, targetSubID, stringBuilder);
        addToConfig(TARGETLOCID, targetLocationID, stringBuilder);
        addToConfig(SETTING_DEFAULT_APPL_VER_ID, defaultApplVerID, stringBuilder);
        addToConfig(SETTING_DATA_DICTIONARY, dataDictionary, stringBuilder);
        addToConfig(SETTING_APP_DATA_DICTIONARY, appDataDictionary, stringBuilder);
        addToConfig(SETTING_TRANSPORT_DATA_DICTIONARY, transportDataDictionary, stringBuilder);
        return stringBuilder;
    }

    private long requirePositive(String tagName, long tagValue) {
        if (tagValue < 0) {
            throw new IllegalArgumentException(tagName + " must not be negative.");
        }
        return tagValue;
    }

    public String getBeginString() {
        return beginString;
    }

    public void setBeginString(String beginString) {
        this.beginString = requireNotNullOrBlank(BEGINSTRING, beginString);
    }

    public String getSocketConnectHost() {
        return socketConnectHost;
    }

    public void setSocketConnectHost(String socketConnectHost) {
        this.socketConnectHost = requireNotNullOrBlank(SETTING_SOCKET_CONNECT_HOST, socketConnectHost);
    }

    public Long getSocketConnectPort() {
        return socketConnectPort;
    }

    public void setSocketConnectPort(Long socketConnectPort) {
        if (socketConnectPort < 1024 || socketConnectPort > 65535) {
            throw new IllegalArgumentException("SocketConnectPort must be in range from 1024 to 65535.");
        }
        this.socketConnectPort = socketConnectPort;
    }

    public String getTargetCompID() {
        return targetCompID;
    }

    public void setTargetCompID(String targetCompID) {
        this.targetCompID = requireNotNullOrBlank(TARGETCOMPID, targetCompID);
    }

    public String getDefaultApplVerID() {
        return defaultApplVerID;
    }

    public void setDefaultApplVerID(String defaultApplVerID) {
        this.defaultApplVerID = requireNotNullOrBlank(SETTING_DEFAULT_APPL_VER_ID, defaultApplVerID);
    }

    public String getTargetSubID() {
        return targetSubID;
    }

    public void setTargetSubID(String targetSubID) {
        this.targetSubID = requireNotNullOrBlank(TARGETSUBID, targetSubID);
    }

    public String getTargetLocationID() {
        return targetLocationID;
    }

    public void setTargetLocationID(String targetLocationID) {
        this.targetLocationID = requireNotNullOrBlank(TARGETLOCID, targetLocationID);
    }

    public String getEncryptPassword() {
        return encryptPassword;
    }

    public void setEncryptPassword(String encryptPassword) {
        this.encryptPassword = convertFromBoolToYOrN("EncryptPassword", encryptPassword);
    }

    public void setFileStorePath(String fileStorePath) {
        this.fileStorePath = requireNotNullOrBlank(SETTING_FILE_STORE_PATH, fileStorePath);
    }

    public void setFileLogPath(String fileLogPath) {
        this.fileLogPath = requireNotNullOrBlank(SETTING_FILE_LOG_PATH, fileLogPath);
    }

    public void setReconnectInterval(Long reconnectInterval) {
        if (!autorelogin) {
            this.reconnectInterval = 1_000_000_000L;
        } else {
            this.reconnectInterval = requirePositive(SETTING_RECONNECT_INTERVAL, reconnectInterval);
        }
    }

    public void setHeartBtInt(Long heartBtInt) {
        this.heartBtInt = requirePositive(SETTING_HEARTBTINT, heartBtInt);
    }

    public void setValidateUserDefinedFields(String validateUserDefinedFields) {
        this.validateUserDefinedFields = convertFromBoolToYOrN(SETTING_VALIDATE_USER_DEFINED_FIELDS, validateUserDefinedFields);
    }

    public void setValidateIncomingMessage(String validateIncomingMessage) {
        this.validateIncomingMessage = convertFromBoolToYOrN(SETTING_VALIDATE_INCOMING_MESSAGE, validateIncomingMessage);
    }

    public void setRefreshOnLogon(String refreshOnLogon) {
        this.refreshOnLogon = convertFromBoolToYOrN(SETTING_REFRESH_ON_LOGON, refreshOnLogon);
    }

//    public void setNonStopSession(String nonStopSession) {
//        this.nonStopSession = convertFromBoolToYOrN(SETTING_NON_STOP_SESSION, nonStopSession);
//    }

    public void setResetOnLogon(String resetOnLogon) {
        this.resetOnLogon = convertFromBoolToYOrN(SETTING_RESET_ON_LOGON, resetOnLogon);
    }

    public void setResetOnLogout(String resetOnLogout) {
        this.resetOnLogout = convertFromBoolToYOrN(SETTING_RESET_ON_LOGOUT, resetOnLogout);
    }

    public void setResetOnDisconnect(String resetOnDisconnect) {
        this.resetOnDisconnect = convertFromBoolToYOrN(SETTING_RESET_ON_DISCONNECT, resetOnDisconnect);
    }

    public void setLogHeartBeats(String logHeartBeats) {
        this.logHeartBeats = convertFromBoolToYOrN(SETTING_LOG_HEARTBEATS, logHeartBeats);
    }

    public void setCheckLatency(String checkLatency) {
        this.checkLatency = convertFromBoolToYOrN(SETTING_CHECK_LATENCY, checkLatency);
    }

    public void setMaxLatency(Long maxLatency) {
        this.maxLatency = requirePositive(SETTING_MAX_LATENCY, maxLatency);
    }

    public void setAllowUnknownMsgFields(String allowUnknownMsgFields) {
        this.allowUnknownMsgFields = convertFromBoolToYOrN(SETTING_ALLOW_UNKNOWN_MSG_FIELDS, allowUnknownMsgFields);
    }

    public void setRejectInvalidMessage(String rejectInvalidMessage) {
        this.rejectInvalidMessage = convertFromBoolToYOrN(SETTING_REJECT_INVALID_MESSAGE, rejectInvalidMessage);
    }

    public void setValidateFieldsOutOfOrder(String validateFieldsOutOfOrder) {
        this.validateFieldsOutOfOrder = convertFromBoolToYOrN(SETTING_VALIDATE_FIELDS_OUT_OF_ORDER, validateFieldsOutOfOrder);
    }

    public void setValidateFieldsHaveValues(String validateFieldsHaveValues) {
        this.validateFieldsHaveValues = convertFromBoolToYOrN(SETTING_VALIDATE_FIELDS_HAVE_VALUES, validateFieldsHaveValues);
    }

    public void setSocketUseSSL(String socketUseSSL) {
        this.socketUseSSL = convertFromBoolToYOrN(SETTING_USE_SSL, socketUseSSL);
    }

    public void setFakeResendRequest(String fakeResendRequest) {
        this.fakeResendRequest = convertFromBoolToYOrN("FakeResendRequest", fakeResendRequest);
    }

    public void setValidateSequenceNumbers(String validateSequenceNumbers) {
        this.validateSequenceNumbers = convertFromBoolToYOrN(SETTING_VALIDATE_SEQUENCE_NUMBERS, validateSequenceNumbers);
    }

    public void setTimeStampPrecision(String timeStampPrecision) {
        this.timeStampPrecision = requireNotNullOrBlank(SETTING_TIMESTAMP_PRECISION, timeStampPrecision);
    }

    public void setEnableNextExpectedMsgSeqNum(String enableNextExpectedMsgSeqNum) {
        this.enableNextExpectedMsgSeqNum = convertFromBoolToYOrN(SETTING_ENABLE_NEXT_EXPECTED_MSG_SEQ_NUM, enableNextExpectedMsgSeqNum);
    }

    public void setOrderingFields(String orderingFields) {
        this.orderingFields = convertFromBoolToYOrN("OrderingFields", orderingFields);
    }

    public void setAutorelogin(boolean autorelogin) {
        this.autorelogin = autorelogin;
        if (!autorelogin) {
            this.reconnectInterval = 1_000_000_000L;
        }
    }

    public void setUseDefaultApplVerID(boolean useDefaultApplVerID) {
        this.useDefaultApplVerID = useDefaultApplVerID;
    }

    public boolean isUseDefaultApplVerID() {
        return useDefaultApplVerID;
    }

    public void setCheckRequiredTags(String checkRequiredTags) {
        this.checkRequiredTags = convertFromBoolToYOrN("CheckRequiredTags", checkRequiredTags);
    }

    public void setPersistMessages(String persistMessages) {
        this.persistMessages = convertFromBoolToYOrN(SETTING_PERSIST_MESSAGES, persistMessages);
    }

    public String getPersistMessages() {
        return persistMessages;
    }

    public String getValidateFieldsOutOfRange() {
        return validateFieldsOutOfRange;
    }

    public void setValidateFieldsOutOfRange(String validateFieldsOutOfRange) {
        this.validateFieldsOutOfRange = convertFromBoolToYOrN(SETTING_VALIDATE_FIELDS_OUT_OF_ORDER, validateFieldsOutOfRange);
    }

    public String getDuplicateTagsAllowed() {
        return duplicateTagsAllowed;
    }

    public void setDuplicateTagsAllowed(String duplicateTagsAllowed) {
        this.duplicateTagsAllowed = convertFromBoolToYOrN(SETTING_DUPLICATE_TAGS_ALLOWED, duplicateTagsAllowed);
    }

    public String getIgnoreAbsenceOf141tag() {
        return ignoreAbsenceOf141tag;
    }

    public void setIgnoreAbsenceOf141tag(String ignoreAbsenceOf141tag) {
        this.ignoreAbsenceOf141tag = convertFromBoolToYOrN(SETTING_IGNORE_ABSENCE_OF_141_TAG, ignoreAbsenceOf141tag);
    }


    public String getCheckRequiredTags() {
        return checkRequiredTags;
    }

    public String getTimeStampPrecision() {
        return timeStampPrecision;
    }

    public String getEnableNextExpectedMsgSeqNum() {
        return enableNextExpectedMsgSeqNum;
    }

    public String getFakeResendRequest() {
        return fakeResendRequest;
    }

    public void setLogonTimeout(Long logonTimeout) {
        this.logonTimeout = requirePositive(SETTING_LOGON_TIMEOUT, logonTimeout);
    }

    public void setLogoutTimeout(Long logoutTimeout) {
        this.logoutTimeout = requirePositive(SETTING_LOGOUT_TIMEOUT, logoutTimeout);
    }

    public void setRequiresOrigSendingTime(String requiresOrigSendingTime) {
        this.requiresOrigSendingTime = convertFromBoolToYOrN(SETTING_REQUIRES_ORIG_SENDING_TIME, requiresOrigSendingTime);
    }

    public void setStartTime(String startTime) {
        this.startTime = requireNotNullOrBlank(SETTING_START_TIME, startTime);
    }

    public void setEndTime(String endTime) {
        this.endTime = requireNotNullOrBlank(SETTING_END_TIME, endTime);
    }

    public void setStartDay(String startDay) {
        this.startDay = requireNotNullOrBlank(SETTING_START_DAY, startDay);
    }

    public void setEndDay(String endDay) {
        this.endDay = requireNotNullOrBlank(SETTING_END_DAY, endDay);
    }

    public void setDefaultCstmApplVerID(String defaultCstmApplVerID) {
        this.defaultCstmApplVerID = requireNotNullOrBlank("DefaultCstmApplVerID", defaultCstmApplVerID);
    }

    public void setSeqNumberFromLogoutRegexp(String seqNumberFromLogoutRegexp) {
        this.seqNumberFromLogoutRegexp = seqNumberFromLogoutRegexp;
    }

    public void setSeqNumberFromRejectRegexp(String seqNumberFromRejectRegexp) {
        this.seqNumberFromRejectRegexp = seqNumberFromRejectRegexp;
    }

    public String getSeqNumberFromRejectRegexp() {
        return seqNumberFromRejectRegexp;
    }

    public String getSeqNumberFromLogoutRegexp() {
        return seqNumberFromLogoutRegexp;
    }

    public String getDefaultCstmApplVerID() {
        return defaultCstmApplVerID;
    }

    public String getOrderingFields() {
        return orderingFields;
    }

    public String getRequiresOrigSendingTime() {
        return requiresOrigSendingTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStartDay() {
        return startDay;
    }

    public String getEndDay() {
        return endDay;
    }

    public Long getLogoutTimeout() {
        return logoutTimeout;
    }

    public Long getLogonTimeout() {
        return logonTimeout;
    }

    public String getValidateSequenceNumbers() {
        return validateSequenceNumbers;
    }

    public void setCipherSuites(String cipherSuites) {
        this.cipherSuites = cipherSuites;
    }

    public String getCipherSuites() {
        return cipherSuites;
    }

    public void setEnabledProtocols(String enabledProtocols) {
        this.enabledProtocols = enabledProtocols;
    }

    public String getEnabledProtocols() {
        return enabledProtocols;
    }

    public void setSocketKeyStorePassword(String socketKeyStorePassword) {
        this.socketKeyStorePassword = socketKeyStorePassword;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = requireNotNullOrBlank(SETTING_TIMEZONE, timeZone);
    }

    public String getSocketKeyStorePassword() {
        return socketKeyStorePassword;
    }

    public void setSocketKeyStore(String socketKeyStore) {
        this.socketKeyStore = socketKeyStore;
    }

    public String getSocketKeyStore() {
        return socketKeyStore;
    }

    public String getSocketUseSSL() {
        return socketUseSSL;
    }

    public String getValidateFieldsHaveValues() {
        return validateFieldsHaveValues;
    }


    public String getValidateFieldsOutOfOrder() {
        return validateFieldsOutOfOrder;
    }

    public String getRejectInvalidMessage() {
        return rejectInvalidMessage;
    }

    public String getAllowUnknownMsgFields() {
        return allowUnknownMsgFields;
    }

    public Long getMaxLatency() {
        return maxLatency;
    }

    public String getCheckLatency() {
        return checkLatency;
    }

    public String getLogHeartBeats() {
        return logHeartBeats;
    }

    public String getResetOnLogout() {
        return resetOnLogout;
    }

    public String getResetOnDisconnect() {
        return resetOnDisconnect;
    }

    public String getResetOnLogon() {
        return resetOnLogon;
    }

    public Long getHeartBtInt() {
        return heartBtInt;
    }

    public String getNonStopSession() {
        return nonStopSession;
    }

    public String getFileStorePath() {
        return fileStorePath;
    }

    public String getFileLogPath() {
        return fileLogPath;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public long getReconnectInterval() {
        return reconnectInterval;
    }

    public String getUseDataDictionary() {
        return useDataDictionary;
    }

    public String getValidateUserDefinedFields() {
        return validateUserDefinedFields;
    }

    public String getValidateIncomingMessage() {
        return validateIncomingMessage;
    }

    public String getRefreshOnLogon() {
        return refreshOnLogon;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public boolean isAutorelogin() {
        return autorelogin;
    }

    public Path getDataDictionary() {
        return dataDictionary;
    }

    public void setDataDictionary(Path dataDictionary) {
        this.dataDictionary = Path.of(requireNotNullOrBlank(SETTING_DATA_DICTIONARY, dataDictionary.toString()));
    }

    public Path getAppDataDictionary() {
        return appDataDictionary;
    }

    public void setAppDataDictionary(Path appDataDictionary) {
        this.appDataDictionary = Path.of(requireNotNullOrBlank(SETTING_APP_DATA_DICTIONARY, appDataDictionary.toString()));
    }

    public Path getTransportDataDictionary() {
        return transportDataDictionary;
    }

    public void setTransportDataDictionary(Path transportDataDictionary) {
        this.transportDataDictionary = Path.of(requireNotNullOrBlank(SETTING_TRANSPORT_DATA_DICTIONARY, transportDataDictionary.toString()));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append(SETTING_FILE_STORE_PATH, fileStorePath)
                .append(SETTING_FILE_LOG_PATH, fileLogPath)
                .append(SETTING_CONNECTION_TYPE, connectionType)
                .append(SETTING_RECONNECT_INTERVAL, reconnectInterval)
                .append(SETTING_HEARTBTINT, heartBtInt)
                .append(SETTING_USE_DATA_DICTIONARY, useDataDictionary)
                .append(SETTING_VALIDATE_USER_DEFINED_FIELDS, validateUserDefinedFields)
                .append(SETTING_VALIDATE_INCOMING_MESSAGE, validateIncomingMessage)
                .append(SETTING_REFRESH_ON_LOGON, refreshOnLogon)
                .append(SETTING_NON_STOP_SESSION, nonStopSession)
                .append(SETTING_RESET_ON_LOGON, resetOnLogon)
                .append(SETTING_RESET_ON_LOGOUT, resetOnLogout)
                .append(SETTING_RESET_ON_DISCONNECT, resetOnDisconnect)
                .append(SETTING_LOG_HEARTBEATS, logHeartBeats)
                .append(SETTING_CHECK_LATENCY, checkLatency)
                .append(SETTING_MAX_LATENCY, maxLatency)
                .append(SETTING_ALLOW_UNKNOWN_MSG_FIELDS, allowUnknownMsgFields)
                .append(SETTING_REJECT_INVALID_MESSAGE, rejectInvalidMessage)
                .append(SETTING_VALIDATE_FIELDS_OUT_OF_ORDER, validateFieldsOutOfOrder)
                .append(SETTING_VALIDATE_FIELDS_HAVE_VALUES, validateFieldsHaveValues)
                .append(SETTING_USE_SSL, socketUseSSL)
                .append(SETTING_KEY_STORE_NAME, socketKeyStore)
                .append(SETTING_KEY_STORE_PWD, socketKeyStorePassword)
                .append(SETTING_ENABLED_PROTOCOLS, enabledProtocols)
                .append(SETTING_CIPHER_SUITES, cipherSuites)
                .append(SETTING_VALIDATE_SEQUENCE_NUMBERS, validateSequenceNumbers)
                .append(SETTING_LOGON_TIMEOUT, logonTimeout)
                .append(SETTING_LOGOUT_TIMEOUT, logoutTimeout)
                .append(SETTING_REQUIRES_ORIG_SENDING_TIME, requiresOrigSendingTime)
                .append(SETTING_TIMEZONE, timeZone)
                .append(SETTING_START_TIME, startTime)
                .append(SETTING_END_TIME, endTime)
                .append(SETTING_START_DAY, startDay)
                .append(SETTING_END_DAY, endDay)
                .append(SETTING_TIMESTAMP_PRECISION, timeStampPrecision)
                .append(SETTING_ENABLE_NEXT_EXPECTED_MSG_SEQ_NUM, enableNextExpectedMsgSeqNum)
                .append(BEGINSTRING, beginString)
                .append(SETTING_SOCKET_CONNECT_HOST, socketConnectHost)
                .append(SETTING_SOCKET_CONNECT_PORT, socketConnectPort)
                .append(TARGETCOMPID, targetCompID)
                .append(TARGETSUBID, targetSubID)
                .append(TARGETLOCID, targetLocationID)
                .append(SETTING_DEFAULT_APPL_VER_ID, defaultApplVerID)
                .append(SETTING_CHECK_REQUIRED_TAGS, checkRequiredTags)
                .append(SETTING_PERSIST_MESSAGES, persistMessages)
                .append(SETTING_VALIDATE_FIELDS_OUT_OF_RANGE, validateFieldsOutOfRange)
                .append(SETTING_DUPLICATE_TAGS_ALLOWED, duplicateTagsAllowed)
                .append(SETTING_IGNORE_ABSENCE_OF_141_TAG, ignoreAbsenceOf141tag)
                .append(SETTING_DATA_DICTIONARY, dataDictionary)
                .append(SETTING_APP_DATA_DICTIONARY, appDataDictionary)
                .append(SETTING_TRANSPORT_DATA_DICTIONARY, transportDataDictionary)
                .append("FakeResendRequest", fakeResendRequest)
                .append("OrderingFields", orderingFields)
                .append("Autorelogine", autorelogin)
                .append("UseDefaultApplVerID", useDefaultApplVerID)
                .append("DefaultCstmApplVerID", defaultCstmApplVerID)
                .append("SeqNumberFromRejectRegexp", seqNumberFromRejectRegexp)
                .append("SeqNumberFromLogoutRegexp", seqNumberFromLogoutRegexp)
                .append("EncryptPassword", encryptPassword)
                .toString();
    }
}
