package com.exactpro.th2.fix.client.fixBean;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static com.exactpro.th2.fix.client.util.FixBeanUtil.addToConfig;
import static com.exactpro.th2.fix.client.util.FixBeanUtil.requireNotNullOrBlank;
import static com.exactpro.th2.fix.client.util.FixBeanUtil.requireYesOrNo;
import static quickfix.Session.*;
import static quickfix.mina.ssl.SSLSupport.*;
import static quickfix.FileLogFactory.*;
import static quickfix.FileStoreFactory.*;
import static quickfix.SessionFactory.*;
import static quickfix.Initiator.*;

public class BaseFixBean {

    protected String fileStorePath = "storage/messages/";
    protected String fileLogPath = "outgoing";
    @JsonIgnore
    protected String connectionType = "initiator";
    protected long reconnectInterval = 60;
    protected long heartBtInt = 30;
    @JsonIgnore
    protected String useDataDictionary = "Y";
    protected String validateUserDefinedFields = "N";
    protected String validateIncomingMessage = "Y";
    protected String refreshOnLogon = "Y";
    protected String nonStopSession = "Y";
    protected String resetOnLogon = "Y";
    protected String resetOnLogout = "N";
    protected String resetOnDisconnect = "N";
    protected String logHeartBeats = "N";
    protected String checkLatency = "N";
    protected long maxLatency = 120;
    protected String allowUnknownMsgFields = "N";
    protected String rejectInvalidMessage = "Y";
    protected String validateFieldsOutOfOrder = "N";
    protected String validateFieldsHaveValues = "Y";
    protected String socketUseSSL = null;
    protected String socketKeyStore = null;
    protected String socketKeyStorePassword = null;
    protected String enabledProtocols = null;
    protected String cipherSuites = null;
    protected String validateSequenceNumbers = "Y";
    protected long logonTimeout = 10;
    protected long logoutTimeout = 10;
    protected String requiresOrigSendingTime = "Y";
    protected String timeZone = null;
    protected String startTime = "00:00:00";
    protected String endTime = "00:00:00";
    protected String startDay = "monday";
    protected String endDay = "sunday";
    protected String timeStampPrecision = null;
    protected String enableNextExpectedMsgSeqNum = null;
    protected String fakeResendRequest = null;


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
        addToConfig(SETTING_RESET_ON_LOGOUT, resetOnLogon, stringBuilder);
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
        addToConfig(SETTING_START_DAY, startDay,stringBuilder);
        addToConfig(SETTING_END_DAY, endDay, stringBuilder);
        addToConfig(SETTING_TIMESTAMP_PRECISION, timeStampPrecision, stringBuilder);
        addToConfig(SETTING_ENABLE_NEXT_EXPECTED_MSG_SEQ_NUM, enableNextExpectedMsgSeqNum, stringBuilder);

        return stringBuilder;
    }

    private long requirePositive(String tagName, long tagValue) {
        if (tagValue < 0) {
            throw new IllegalArgumentException(tagName + " must not be negative.");
        }
        return tagValue;
    }

    public void setFileStorePath(String fileStorePath) {
        this.fileStorePath = requireNotNullOrBlank(SETTING_FILE_STORE_PATH, fileStorePath);
    }

    public void setFileLogPath(String fileLogPath) {
        this.fileLogPath = requireNotNullOrBlank(SETTING_FILE_LOG_PATH, fileLogPath);
    }

    public void setReconnectInterval(long reconnectInterval) {
        this.reconnectInterval = requirePositive(SETTING_RECONNECT_INTERVAL, reconnectInterval);
    }

    public void setHeartBtInt(long heartBtInt) {
        this.heartBtInt = requirePositive(SETTING_HEARTBTINT, heartBtInt);
    }

    public void setValidateUserDefinedFields(String validateUserDefinedFields) {
        this.validateUserDefinedFields = requireYesOrNo(SETTING_VALIDATE_USER_DEFINED_FIELDS, validateUserDefinedFields);
    }

    public void setValidateIncomingMessage(String validateIncomingMessage) {
        this.validateIncomingMessage = requireYesOrNo(SETTING_VALIDATE_INCOMING_MESSAGE, validateIncomingMessage);
    }

    public void setRefreshOnLogon(String refreshOnLogon) {
        this.refreshOnLogon = requireYesOrNo(SETTING_REFRESH_ON_LOGON, refreshOnLogon);
    }

    public void setNonStopSession(String nonStopSession) {
        this.nonStopSession = requireYesOrNo(SETTING_NON_STOP_SESSION, nonStopSession);
    }

    public void setResetOnLogon(String resetOnLogon) {
        this.resetOnLogon = requireYesOrNo(SETTING_RESET_ON_LOGON, resetOnLogon);
    }

    public void setResetOnLogout(String resetOnLogout) {
        this.resetOnLogout = requireYesOrNo(SETTING_RESET_ON_LOGOUT, resetOnLogout);
    }

    public void setResetOnDisconnect(String resetOnDisconnect) {
        this.resetOnDisconnect = requireYesOrNo(SETTING_RESET_ON_DISCONNECT, resetOnDisconnect);
    }

    public void setLogHeartBeats(String fileLogHeartBeats) {
        this.logHeartBeats = requireYesOrNo(SETTING_LOG_HEARTBEATS, fileLogHeartBeats);
    }

    public void setCheckLatency(String checkLatency) {
        this.checkLatency = requireYesOrNo(SETTING_CHECK_LATENCY, checkLatency);
    }

    public void setMaxLatency(long maxLatency) {
        this.maxLatency = requirePositive(SETTING_MAX_LATENCY, maxLatency);
    }

    public void setAllowUnknownMsgFields(String allowUnknownMsgFields) {
        this.allowUnknownMsgFields = requireYesOrNo(SETTING_ALLOW_UNKNOWN_MSG_FIELDS, allowUnknownMsgFields);
    }

    public void setRejectInvalidMessage(String rejectInvalidMessage) {
        this.rejectInvalidMessage = requireYesOrNo(SETTING_REJECT_INVALID_MESSAGE, rejectInvalidMessage);
    }

    public void setValidateFieldsOutOfOrder(String validateFieldsOutOfOrder) {
        this.validateFieldsOutOfOrder = requireYesOrNo(SETTING_VALIDATE_FIELDS_OUT_OF_ORDER, validateFieldsOutOfOrder);
    }

    public void setValidateFieldsHaveValues(String validateFieldsHaveValues) {
        this.validateFieldsHaveValues = requireYesOrNo(SETTING_VALIDATE_FIELDS_HAVE_VALUES, validateFieldsHaveValues);
    }

    public void setSocketUseSSL(String socketUseSSL) {
        this.socketUseSSL = requireYesOrNo(SETTING_USE_SSL, socketUseSSL);
    }

    public void setFakeResendRequest(String fakeResendRequest) {
        this.fakeResendRequest = requireYesOrNo("FakeResendRequest", fakeResendRequest);
    }

    public void setValidateSequenceNumbers(String validateSequenceNumbers) {
        this.validateSequenceNumbers = requireYesOrNo(SETTING_VALIDATE_SEQUENCE_NUMBERS, validateSequenceNumbers);
    }

    public void setTimeStampPrecision(String timeStampPrecision) {
        this.timeStampPrecision = requireNotNullOrBlank(SETTING_TIMESTAMP_PRECISION, timeStampPrecision);

    }

    public void setEnableNextExpectedMsgSeqNum(String enableNextExpectedMsgSeqNum) {
        this.enableNextExpectedMsgSeqNum = requireYesOrNo(SETTING_ENABLE_NEXT_EXPECTED_MSG_SEQ_NUM, enableNextExpectedMsgSeqNum);
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

    public void setLogonTimeout(long logonTimeout) {
        this.logonTimeout = requirePositive(SETTING_LOGON_TIMEOUT, logonTimeout);
    }

    public void setLogoutTimeout(long logoutTimeout) {
        this.logoutTimeout = requirePositive(SETTING_LOGOUT_TIMEOUT, logoutTimeout);
    }

    public void setRequiresOrigSendingTime(String requiresOrigSendingTime) {
        this.requiresOrigSendingTime = requireYesOrNo(SETTING_REQUIRES_ORIG_SENDING_TIME, requiresOrigSendingTime);
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

    public long getLogoutTimeout() {
        return logoutTimeout;
    }

    public long getLogonTimeout() {
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

    public long getMaxLatency() {
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

    public long getHeartBtInt() {
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
                .append(SETTING_RESET_ON_LOGOUT, resetOnLogon)
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
                .append("FakeResendRequest", fakeResendRequest)

                .toString();
    }
}
