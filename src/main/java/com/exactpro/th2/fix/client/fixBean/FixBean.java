package com.exactpro.th2.fix.client.fixBean;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static com.exactpro.th2.fix.client.util.FixBeanUtil.addToConfig;

public class FixBean {

    @JsonProperty(required = true)
    protected String fileStorePath = "storage/messages/";
    @JsonProperty(required = true)
    protected String fileLogPath = "outgoing";
    @JsonIgnore
    protected String connectionType = "initiator";
    @JsonProperty(required = true)
    protected long reconnectInterval = 60;
    @JsonProperty(required = true)
    protected long heartBtInt = 30;
    @JsonIgnore
    protected String useDataDictionary = "Y";
    protected String validateUserDefinedFields = "N";
    protected String validateIncomingMessage = "N";
    protected String refreshOnLogon = "Y";
    @JsonProperty(required = true)
    protected String nonStopSession = "Y";

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
    @JsonProperty(required = true)
    protected String targetCompID = null;
    protected String targetSubID = null;
    protected String targetLocationID = null;
    protected String dataDictionary = null;
    @JsonIgnore
    protected String sessionQualifier = null;
    protected String sessionAlias = null;

    public FixBean() {
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
        addToConfig("BeginString", beginString, sb);
        addToConfig("SocketConnectHost", socketConnectHost, sb);
        addToConfig("SocketConnectPort", socketConnectPort, sb);
        addToConfig("SenderCompID", senderCompID, sb);
        addToConfig("SenderSubID", senderSubID, sb);
        addToConfig("SenderLocationID", senderLocationID, sb);
        addToConfig("TargetCompID", targetCompID, sb);
        addToConfig("TargetSubID", targetSubID, sb);
        addToConfig("TargetLocationID", targetLocationID, sb);
        addToConfig("DataDictionary", dataDictionary, sb);

        return sb;
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
                .append("BeginString", beginString)
                .append("SocketConnectHost", socketConnectHost)
                .append("SocketConnectPort", socketConnectPort)
                .append("SenderCompID", senderCompID)
                .append("SenderSubID", senderSubID)
                .append("SenderLocationID", senderLocationID)
                .append("TargetCompID", targetCompID)
                .append("TargetSubID", targetSubID)
                .append("TargetLocationID", targetLocationID)
                .append("DataDictionary", dataDictionary)
                .append("SessionQualifier", sessionQualifier)
                .append("SessionAlias", sessionAlias)
                .toString();
    }

    public void setConnectionType(String connectionType) {
        if (connectionType == null || connectionType.equals("")) {
            throw new IllegalArgumentException("connectionType must be only initiator.");
        }
        this.connectionType = connectionType;
    }

    public void setFileStorePath(String fileStorePath) {
        if (fileStorePath == null || fileStorePath.equals("")) {
            throw new IllegalArgumentException("fileStorePath must not be null or blank.");
        }
        this.fileStorePath = fileStorePath;
    }

    public void setFileLogPath(String fileLogPath) {
        if (fileLogPath == null || fileLogPath.equals("")) {
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

    public void setUseDataDictionary(String useDataDictionary) {
        if (useDataDictionary == null || useDataDictionary.equals("")) {
            throw new IllegalArgumentException("UseDataDictionary must be \"Y\" or \"N\".");
        }
        this.useDataDictionary = useDataDictionary;
    }

    public void setValidateUserDefinedFields(String validateUserDefinedFields) {
        if (validateUserDefinedFields == null || validateUserDefinedFields.equals("")) {
            throw new IllegalArgumentException("validateUserDefinedFields must be \"Y\" or \"N\".");
        }
        this.validateUserDefinedFields = validateUserDefinedFields;
    }

    public void setValidateIncomingMessage(String validateIncomingMessage) {
        if (validateIncomingMessage == null || validateIncomingMessage.equals("")) {
            throw new IllegalArgumentException("validateIncomingMessage must be \"Y\" or \"N\".");
        }
        this.validateIncomingMessage = validateIncomingMessage;
    }

    public void setRefreshOnLogon(String refreshOnLogon) {
        if (refreshOnLogon == null || refreshOnLogon.equals("")) {
            throw new IllegalArgumentException("refreshOnLogon must be \"Y\" or \"N\".");
        }
        this.refreshOnLogon = refreshOnLogon;
    }

    public void setNonStopSession(String nonStopSession) {
        if (nonStopSession == null || nonStopSession.equals("")) {
            throw new IllegalArgumentException("nonStopSession must be \"Y\" or \"N\".");
        }
        this.nonStopSession = nonStopSession;
    }

    public void setBeginString(String beginString) {
        if (beginString == null || beginString.equals("")) {
            throw new IllegalArgumentException("beginString must not be null or blank.");
        }
        this.beginString = beginString;
    }

    public void setSocketConnectHost(String socketConnectHost) {
        if (socketConnectHost == null || socketConnectHost.equals("")) {
            throw new IllegalArgumentException("socketConnectHost must not be null or blank.");
        }
        this.socketConnectHost = socketConnectHost;
    }

    public void setTargetCompID(String targetCompID) {
        if (targetCompID == null || targetCompID.equals("")) {
            throw new IllegalArgumentException("TargetCompID must not be null or blank.");
        }
        this.targetCompID = targetCompID;
    }

    public void setSocketConnectPort(long socketConnectPort) {
        if (socketConnectPort < 1024 || socketConnectPort > 65535) {
            throw new IllegalArgumentException("SocketConnectPort must be in range from 1024 to 65535.");
        }
        this.socketConnectPort = socketConnectPort;
    }

    public void setSenderCompID(String senderCompID) {
        if (senderCompID == null || senderCompID.equals("")) {
            throw new IllegalArgumentException("SenderCompId must not be null or blank.");
        }
        this.senderCompID = senderCompID;
    }

    public void setDataDictionary(String dataDictionary) {
        if (dataDictionary == null || dataDictionary.equals("")) {
            throw new IllegalArgumentException("DataDictionary must not be null or blank.");
        }
        this.dataDictionary = dataDictionary;
    }

    public void setSessionAlias(String sessionAlias) {
        if (sessionAlias == null || sessionAlias.equals("")) {
            throw new IllegalArgumentException("SessionAlias must not be null or blank.");
        }
        this.sessionAlias = sessionAlias;
    }

    public void setSessionQualifier(String sessionQualifier) {
        if (sessionQualifier == null || sessionQualifier.equals("")) {
            throw new IllegalArgumentException("SessionQualifier must not be null or blank.");
        }
        this.sessionQualifier = sessionQualifier;
    }

    public void setSenderSubID(String senderSubID) {
        if (senderSubID == null || senderSubID.equals("")) {
            throw new IllegalArgumentException("SenderSubID must not be null or blank.");
        }
        this.senderSubID = senderSubID;
    }

    public void setSenderLocationID(String senderLocationID) {
        if (senderLocationID == null || senderLocationID.equals("")) {
            throw new IllegalArgumentException("SenderLocationID must not be null or blank.");
        }
        this.senderLocationID = senderLocationID;
    }

    public void setTargetSubID(String targetSubID) {
        if (targetSubID == null || targetSubID.equals("")) {
            throw new IllegalArgumentException("TargetSubID must not be null or blank.");
        }
        this.targetSubID = targetSubID;
    }

    public void setTargetLocationID(String targetLocationID) {
        if (targetLocationID == null || targetLocationID.equals("")) {
            throw new IllegalArgumentException("TargetLocationID must not be null or blank.");
        }
        this.targetLocationID = targetLocationID;
    }

    public String getSessionAlias() {
        return sessionAlias;
    }

    public String getSenderSubID() {
        return senderSubID;
    }

    public String getSessionQualifier() {
        return sessionQualifier;
    }

    public String getSenderLocationID() {
        return senderLocationID;
    }

    public String getTargetSubID() {
        return targetSubID;
    }

    public String getTargetLocationID() {
        return targetLocationID;
    }

    public String getDataDictionary() {
        return dataDictionary;
    }

    public long getHeartBtInt() {
        return heartBtInt;
    }

    public String getNonStopSession() {
        return nonStopSession;
    }

    public String getBeginString() {
        return beginString;
    }

    public String getSenderCompID() {
        return senderCompID;
    }

    public String getTargetCompID() {
        return targetCompID;
    }

    public String getSocketConnectHost() {
        return socketConnectHost;
    }

    public long getSocketConnectPort() {
        return socketConnectPort;
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

}
