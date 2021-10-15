package com.exactpro.th2.fix.client.fixBean;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static com.exactpro.th2.fix.client.util.FixBeanUtil.addToConfig;
import static com.exactpro.th2.fix.client.util.FixBeanUtil.requireNotNullOrBlank;

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
    protected String validateFieldsOutOfOrder = "Y";
    protected String validateFieldsHaveValues = "Y";
    protected String socketUseSSL = "N";
    protected String socketKeyStore = "";
    protected String socketKeyStorePassword = "";
    protected String enabledProtocols = "";
    protected String cipherSuites = "";
    protected String validateSequenceNumbers = "Y";
    protected long logonTimeout = 10;
    protected long logoutTimeout = 10;
    protected String requiresOrigSendingTime = "Y";


    public BaseFixBean() {
    }

    public StringBuilder toConfig(String sectionName) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(sectionName).append("]").append(System.lineSeparator());
        addToConfig("FileStorePath", fileStorePath, sb);
        addToConfig("FileLogPath", fileLogPath, sb);
        addToConfig("ConnectionType", connectionType, sb);
        addToConfig("ReconnectInterval", reconnectInterval, sb);
        addToConfig("NonStopSession", nonStopSession, sb);
        addToConfig("HeartBtInt", heartBtInt, sb);
        addToConfig("UseDataDictionary", useDataDictionary, sb);
        addToConfig("ValidateUserDefinedFields", validateUserDefinedFields, sb);
        addToConfig("ValidateIncomingMessage", validateIncomingMessage, sb);
        addToConfig("RefreshOnLogon", refreshOnLogon, sb);
        addToConfig("ResetOnLogon", resetOnLogon, sb);
        addToConfig("ResetOnLogout", resetOnLogon, sb);
        addToConfig("ResetOnDisconnect", resetOnDisconnect, sb);
        addToConfig("LogHeartBeats", logHeartBeats, sb);
        addToConfig("CheckLatency", checkLatency, sb);
        addToConfig("MaxLatency", maxLatency, sb);
        addToConfig("AllowUnknownMsgFields", allowUnknownMsgFields, sb);
        addToConfig("RejectInvalidMessage", rejectInvalidMessage, sb);
        addToConfig("ValidateFieldsOutOfOrder", validateFieldsOutOfOrder, sb);
        addToConfig("ValidateFieldsHaveValues", validateFieldsHaveValues, sb);
        addToConfig("SocketUseSSL", socketUseSSL, sb);
        addToConfig("SocketKeyStore", socketKeyStore, sb);
        addToConfig("SocketKeyStorePassword", socketKeyStorePassword, sb);
        addToConfig("EnabledProtocols", enabledProtocols, sb);
        addToConfig("CipherSuites", cipherSuites, sb);
        addToConfig("ValidateSequenceNumbers", validateSequenceNumbers, sb);
        addToConfig("LogonTimeout", logonTimeout, sb);
        addToConfig("LogoutTimeout", logoutTimeout, sb);
        addToConfig("RequiresOrigSendingTime", requiresOrigSendingTime, sb);
        return sb;
    }

    private String requireYesOrNo(String tagName, String tagValue) {
        if (!tagValue.equals("Y") && !tagValue.equals("N")) {
            throw new IllegalArgumentException(tagName + " must be \"Y\" or \"N\".");
        }
        return tagValue;
    }

    private long requirePositive(String tagName, long tagValue) {
        if (tagValue < 0) {
            throw new IllegalArgumentException(tagName + " must not be negative.");
        }
        return tagValue;
    }

    public void setFileStorePath(String fileStorePath) {
        this.fileStorePath = requireNotNullOrBlank("FileStorePath", fileStorePath);
    }

    public void setFileLogPath(String fileLogPath) {
        this.fileLogPath = requireNotNullOrBlank("FileLogFile", fileLogPath);
    }

    public void setReconnectInterval(long reconnectInterval) {
        this.reconnectInterval = requirePositive("ReconnectionInterval", reconnectInterval);
    }

    public void setHeartBtInt(long heartBtInt) {
        this.heartBtInt = requirePositive("HeartBtInt", heartBtInt);
    }

    public void setValidateUserDefinedFields(String validateUserDefinedFields) {
        this.validateUserDefinedFields = requireYesOrNo("ValidateUserDefinedFields", validateUserDefinedFields);
    }

    public void setValidateIncomingMessage(String validateIncomingMessage) {
        this.validateIncomingMessage = requireYesOrNo("ValidateIncomingMessage", validateIncomingMessage);
    }

    public void setRefreshOnLogon(String refreshOnLogon) {
        this.refreshOnLogon = requireYesOrNo("RefreshOnLogon", refreshOnLogon);
    }

    public void setNonStopSession(String nonStopSession) {
        this.nonStopSession = requireYesOrNo("NonStopSession", nonStopSession);
    }

    public void setResetOnLogon(String resetOnLogon) {
        this.resetOnLogon = requireYesOrNo("ResetOnLogon", resetOnLogon);
    }

    public void setResetOnLogout(String resetOnLogout) {
        this.resetOnLogout = requireYesOrNo("ResetOnLogout", resetOnLogout);
    }

    public void setResetOnDisconnect(String resetOnDisconnect) {
        this.resetOnDisconnect = requireYesOrNo("ResetOnDisconnect", resetOnDisconnect);
    }

    public void setLogHeartBeats(String logHeartBeats) {
        this.logHeartBeats = requireYesOrNo("LogHeartBeats", logHeartBeats);
    }

    public void setCheckLatency(String checkLatency) {
        this.checkLatency = requireYesOrNo("CheckLatency", checkLatency);
    }

    public void setMaxLatency(long maxLatency) {
        this.maxLatency = requirePositive("MaxLatency", maxLatency);
    }

    public void setAllowUnknownMsgFields(String allowUnknownMsgFields) {
        this.allowUnknownMsgFields = requireYesOrNo("AllowUnknownMsgFields", allowUnknownMsgFields);
    }

    public void setRejectInvalidMessage(String rejectInvalidMessage) {
        this.rejectInvalidMessage = requireYesOrNo("RejectInvalidMessage", rejectInvalidMessage);
    }

    public void setValidateFieldsOutOfOrder(String validateFieldsOutOfOrder) {
        this.validateFieldsOutOfOrder = requireYesOrNo("ValidateFieldsOutOfOrder", validateFieldsOutOfOrder);
    }

    public void setValidateFieldsHaveValues(String validateFieldsHaveValues) {
        this.validateFieldsHaveValues = requireYesOrNo("ValidateFieldsHaveValues", validateFieldsHaveValues);
    }

    public void setSocketUseSSL(String socketUseSSL) {
        this.socketUseSSL = requireYesOrNo("SocketUseSSL", socketUseSSL);
    }

    public void setValidateSequenceNumbers(String validateSequenceNumbers) {
        this.validateSequenceNumbers = requireYesOrNo("ValidateSequenceNumbers", validateSequenceNumbers);
    }

    public void setLogonTimeout(long logonTimeout) {
        this.logonTimeout = requirePositive("LogonTimeout", logonTimeout);
    }

    public void setLogoutTimeout(long logoutTimeout) {
        this.logoutTimeout = requirePositive("LogoutTimeout", logoutTimeout);
    }

    public void setRequiresOrigSendingTime(String requiresOrigSendingTime) {
        this.requiresOrigSendingTime = requireYesOrNo("RequiresOrigSendingTime", requiresOrigSendingTime);
    }

    public String getRequiresOrigSendingTime() {
        return requiresOrigSendingTime;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("FileStorePath", fileStorePath)
                .append("FileLogPath", fileLogPath)
                .append("ConnectionType", connectionType)
                .append("ReconnectInterval", reconnectInterval)
                .append("HeartBtInt", heartBtInt)
                .append("UseDataDictionary", useDataDictionary)
                .append("ValidateUserDefinedFields", validateUserDefinedFields)
                .append("ValidateIncomingMessage", validateIncomingMessage)
                .append("RefreshOnLogon", refreshOnLogon)
                .append("NonStopSession", nonStopSession)
                .append("ResetOnLogon", resetOnLogon)
                .append("ResetOnLogout", resetOnLogon)
                .append("ResetOnDisconnect", resetOnDisconnect)
                .append("LogHeartBeats", logHeartBeats)
                .append("CheckLatency", checkLatency)
                .append("MaxLatency", maxLatency)
                .append("AllowUnknownMsgFields", allowUnknownMsgFields)
                .append("RejectInvalidMessage", rejectInvalidMessage)
                .append("ValidateFieldsOutOfOrder", validateFieldsOutOfOrder)
                .append("ValidateFieldsHaveValues", validateFieldsHaveValues)
                .append("SocketUseSSL", socketUseSSL)
                .append("SocketKeyStore", socketKeyStore)
                .append("SocketKeyStorePassword", socketKeyStorePassword)
                .append("EnabledProtocols", enabledProtocols)
                .append("CipherSuites", cipherSuites)
                .append("ValidateSequenceNumbers", validateSequenceNumbers)
                .append("LogonTimeout", logonTimeout)
                .append("LogoutTimeout", logoutTimeout)
                .append("RequiresOrigSendingTime", requiresOrigSendingTime)
                .toString();
    }
}
