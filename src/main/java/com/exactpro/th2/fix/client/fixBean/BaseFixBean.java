package com.exactpro.th2.fix.client.fixBean;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static com.exactpro.th2.fix.client.util.FixBeanUtil.addToConfig;

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

    private void validateTagValue(String tagName, String tagValue) {
        if (!tagValue.equals("Y") && !tagValue.equals("N")) {
            throw new IllegalArgumentException(tagName + " must be \"Y\" or \"N\".");
        }
    }

    public void setFileStorePath(String fileStorePath) {
        if (StringUtils.isBlank(fileStorePath)) {
            throw new IllegalArgumentException("fileStorePath must not be null or blank.");
        }
        this.fileStorePath = fileStorePath;
    }

    public void setFileLogPath(String fileLogPath) {
        if (StringUtils.isBlank(fileLogPath)) {
            throw new IllegalArgumentException("FileLogPath must not be null or blank.");
        }
        this.fileLogPath = fileLogPath;
    }

    public void setReconnectInterval(long reconnectInterval) {
        if (reconnectInterval < 0) {
            throw new IllegalArgumentException("Reconnect interval must not be negative.");
        }
        this.reconnectInterval = reconnectInterval;
    }

    public void setHeartBtInt(long heartBtInt) {
        if (heartBtInt < 0) {
            throw new IllegalArgumentException("heartBtInt must not be negative.");
        }
        this.heartBtInt = heartBtInt;
    }

    public void setValidateUserDefinedFields(String validateUserDefinedFields) {
        validateTagValue("ValidateUserDefinedFields", validateUserDefinedFields);
        this.validateUserDefinedFields = validateUserDefinedFields;
    }

    public void setValidateIncomingMessage(String validateIncomingMessage) {
        validateTagValue("ValidateIncomingMessage", validateIncomingMessage);
        this.validateIncomingMessage = validateIncomingMessage;
    }

    public void setRefreshOnLogon(String refreshOnLogon) {
        validateTagValue("RefreshOnLogon", refreshOnLogon);
        this.refreshOnLogon = refreshOnLogon;
    }

    public void setNonStopSession(String nonStopSession) {
        validateTagValue("NonStopSession", nonStopSession);
        this.nonStopSession = nonStopSession;
    }

    public void setResetOnLogon(String resetOnLogon) {
        validateTagValue("ResetOnLogon", resetOnLogon);
        this.resetOnLogon = resetOnLogon;
    }

    public void setResetOnLogout(String resetOnLogout) {
        validateTagValue("ResetOnLogout", resetOnLogout);
        this.resetOnLogout = resetOnLogout;
    }

    public void setResetOnDisconnect(String resetOnDisconnect) {
        validateTagValue("ResetOnDisconnect", resetOnDisconnect);
        this.resetOnDisconnect = resetOnDisconnect;
    }

    public void setLogHeartBeats(String logHeartBeats) {
        validateTagValue("LogHeartBeats", logHeartBeats);
        this.logHeartBeats = logHeartBeats;
    }

    public void setCheckLatency(String checkLatency) {
        validateTagValue("CheckLatency", checkLatency);
        this.checkLatency = checkLatency;
    }

    public void setMaxLatency(long maxLatency) {
        if (maxLatency < 0) {
            throw new IllegalArgumentException("MaxLatency must not be negative.");
        }
        this.maxLatency = maxLatency;
    }

    public void setAllowUnknownMsgFields(String allowUnknownMsgFields) {
        validateTagValue("AllowUnknownMsgFields", allowUnknownMsgFields);
        this.allowUnknownMsgFields = allowUnknownMsgFields;
    }

    public void setRejectInvalidMessage(String rejectInvalidMessage) {
        validateTagValue("RejectInvalidMessage", rejectInvalidMessage);
        this.rejectInvalidMessage = rejectInvalidMessage;
    }

    public void setValidateFieldsOutOfOrder(String validateFieldsOutOfOrder) {
        validateTagValue("ValidateFieldsOutOfOrder", validateFieldsOutOfOrder);
        this.validateFieldsOutOfOrder = validateFieldsOutOfOrder;
    }

    public void setValidateFieldsHaveValues(String validateFieldsHaveValues) {
        validateTagValue("ValidateFieldsHaveValues", validateFieldsHaveValues);
        this.validateFieldsHaveValues = validateFieldsHaveValues;
    }

    public void setSocketUseSSL(String socketUseSSL) {
        validateTagValue("SocketUseSSL", socketUseSSL);
        this.socketUseSSL = socketUseSSL;
    }

    public void setValidateSequenceNumbers(String validateSequenceNumbers) {
        validateTagValue("ValidateSequenceNumbers", validateSequenceNumbers);
        this.validateSequenceNumbers = validateSequenceNumbers;
    }

    public void setLogonTimeout(long logonTimeout) {
        if (logonTimeout < 0) {
            throw new IllegalArgumentException("LogonTimeout must not be negative.");
        }
        this.logonTimeout = logonTimeout;
    }

    public void setLogoutTimeout(long logoutTimeout) {
        if (logonTimeout < 0) {
            throw new IllegalArgumentException("LogoutTimeout must not be negative.");
        }
        this.logoutTimeout = logoutTimeout;
    }

    public void setRequiresOrigSendingTime(String requiresOrigSendingTime) {
        validateTagValue("RequiresOrigSendingTime", requiresOrigSendingTime);
        this.requiresOrigSendingTime = requiresOrigSendingTime;
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
