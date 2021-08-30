package com.exactpro.th2.fix.client.fixBean;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FixBean {

    protected String applicationID = "client";
    protected String fileStorePath = "storage/messages/";
    protected String fileLogPath = "outgoing";
    protected String connectionType = "initiator";
    protected long reconnectInterval = 60;
    protected long heartBtInt = 30;
    protected String useDataDictionary = "Y";
    protected String validateUserDefinedFields = "N";
    protected String validateIncomingMessage = "N";
    protected String refreshOnLogon = "Y";
    protected String nonStopSession = "Y";

    protected String beginString = "FIX.4.2";
    protected String socketConnectHost = "localhost";
    protected long socketConnectPort = 9877;
    protected String senderCompID = null;
    protected String senderSubID = null;
    protected String senderLocationID = null;
    protected String targetCompID = null;
    protected String targetSubID = null;
    protected String targetLocationID = null;
    protected String dataDictionary = null;
    protected String sessionQualifier = null;
    protected String sessionAlias = null;

    public FixBean() {
    }

    public StringBuilder toConfig(String sectionName) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(sectionName).append("]").append(System.lineSeparator());
        sb.append("ApplicationID=").append(applicationID).append(System.lineSeparator());
        sb.append("FileStorePath=").append(fileStorePath).append(System.lineSeparator());
        sb.append("FileLogPath=").append(fileLogPath).append(System.lineSeparator());
        sb.append("ConnectionType=").append(connectionType).append(System.lineSeparator());
        sb.append("ReconnectInterval=").append(reconnectInterval).append(System.lineSeparator());
        sb.append("NonStopSession=").append(nonStopSession).append(System.lineSeparator());
        sb.append("HeartBtInt=").append(heartBtInt).append(System.lineSeparator());
        sb.append("UseDataDictionary=").append(useDataDictionary).append(System.lineSeparator());
        sb.append("ValidateUserDefinedFields=").append(validateUserDefinedFields).append(System.lineSeparator());
        sb.append("ValidateIncomingMessage=").append(validateIncomingMessage).append(System.lineSeparator());
        sb.append("RefreshOnLogon=").append(refreshOnLogon).append(System.lineSeparator());

        if (beginString != null) sb.append("BeginString=").append(beginString).append(System.lineSeparator());
        if (socketConnectHost != null)
            sb.append("SocketConnectHost=").append(socketConnectHost).append(System.lineSeparator());
        if (socketConnectPort != 0)
            sb.append("SocketConnectPort=").append(socketConnectPort).append(System.lineSeparator());
        if (senderCompID != null) sb.append("SenderCompID=").append(senderCompID).append(System.lineSeparator());
        if (senderSubID != null) sb.append("SenderSubID=").append(senderSubID).append(System.lineSeparator());
        if (senderLocationID != null)
            sb.append("SenderLocationID=").append(senderLocationID).append(System.lineSeparator());
        if (targetCompID != null) sb.append("TargetCompID=").append(targetCompID).append(System.lineSeparator());
        if (targetSubID != null) sb.append("TargetSubID=").append(targetSubID).append(System.lineSeparator());
        if (targetLocationID != null)
            sb.append("TargetLocationID=").append(targetLocationID).append(System.lineSeparator());
        if (dataDictionary != null)
            sb.append("DataDictionary=").append(dataDictionary).append(System.lineSeparator());


        return sb;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("ApplicationId", applicationID)
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

    public void setTargetCompID(String targetCompID) {
        if (targetCompID == null || targetCompID.equals(""))
            throw new IllegalArgumentException("TargetCompID must not be null or blank.");
        this.targetCompID = targetCompID;
    }

    public void setSocketConnectPort(long socketConnectPort) {
        if (socketConnectPort == 0) throw new IllegalArgumentException("SocketConnectPort must not be null or blank.");
        this.socketConnectPort = socketConnectPort;
    }


    public void setSenderCompID(String senderCompID) {
        if (senderCompID == null || senderCompID.equals(""))
            throw new IllegalArgumentException("SenderCompId must not be null or blank.");
        this.senderCompID = senderCompID;
    }

    public void setDataDictionary(String dataDictionary) {
        if (dataDictionary == null || dataDictionary.equals(""))
            throw new IllegalArgumentException("DataDictionary must not be null or blank.");
        this.dataDictionary = dataDictionary;
    }

    public String getSessionAlias() {
        return sessionAlias;
    }

    public void setSessionAlias(String sessionAlias) {
        if (sessionAlias == null || sessionAlias.equals(""))
            throw new IllegalArgumentException("SessionAlias must not be null or blank.");
        this.sessionAlias = sessionAlias;
    }

    public String getSenderSubID() {
        return senderSubID;
    }

    public String getSessionQualifier() {
        return sessionQualifier;
    }

    public void setSessionQualifier(String sessionQualifier) {
        if (sessionQualifier == null || sessionQualifier.equals(""))
            throw new IllegalArgumentException("SessionQualifier must not be null or blank.");
        this.sessionQualifier = sessionQualifier;
    }

    public void setSenderSubID(String senderSubID) {
        if (senderSubID == null || senderSubID.equals(""))
            throw new IllegalArgumentException("SenderSubID must not be null or blank.");
        this.senderSubID = senderSubID;
    }

    public String getSenderLocationID() {
        return senderLocationID;
    }

    public void setSenderLocationID(String senderLocationID) {
        if (senderLocationID == null || senderLocationID.equals(""))
            throw new IllegalArgumentException("SenderLocationID must not be null or blank.");
        this.senderLocationID = senderLocationID;
    }

    public String getTargetSubID() {
        return targetSubID;
    }

    public void setTargetSubID(String targetSubID) {
        if (targetSubID == null || targetSubID.equals(""))
            throw new IllegalArgumentException("TargetSubID must not be null or blank.");
        this.targetSubID = targetSubID;
    }

    public String getTargetLocationID() {
        return targetLocationID;
    }

    public void setTargetLocationID(String targetLocationID) {
        if (targetLocationID == null || targetLocationID.equals(""))
            throw new IllegalArgumentException("TargetLocationID must not be null or blank.");
        this.targetLocationID = targetLocationID;
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

    public String getApplicationID() {
        return applicationID;
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
