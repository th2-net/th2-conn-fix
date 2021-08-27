package com.exactpro.th2.fix.client.fixBean;


public class FixBean {

    protected String applicationID = "client";
    protected String fileStorePath = "storage/messages/";
    protected String fileLogPath = "outgoing";
    protected String connectionType = "initiator";
    protected long reconnectInterval = 60;
    protected long heartBtInt = 30;
    protected String useDataDictionary = "Y";
    protected String mmValidateUserDefinedFields = "N";
    protected String validateIncomingMessage = "N";
    protected String refreshOnLogon = "Y";
    protected String nonStopSession = "Y";

    protected String beginString = "FIX.4.2";
    protected String socketConnectHost = "localhost";
    protected long socketConnectPort = 9877;
    protected String senderCompID = "client";
    protected String senderSubID = null;
    protected String senderLocationID = null;
    protected String targetCompID = "server";
    protected String targetSubID = null;
    protected String targetLocationID = null;
    protected String dataDictionary = null;
    protected String sessionQualifier = null;
    protected String sessionAlias = null;

    public FixBean() {
    }

    public StringBuilder toConfig(String sectionName) { //todo mb getting fixBean as argument to fixBean was POJO?
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
        sb.append("ValidateUserDefinedFields=").append(mmValidateUserDefinedFields).append(System.lineSeparator());
        sb.append("ValidateIncomingMessage=").append(validateIncomingMessage).append(System.lineSeparator());
        sb.append("RefreshOnLogon=").append(refreshOnLogon).append(System.lineSeparator());

        if (!sectionName.equals("default")) {
            if (beginString != null) sb.append("BeginString=").append(beginString).append(System.lineSeparator());
            if (socketConnectHost != null)
                sb.append("SocketConnectHost=").append(socketConnectHost).append(System.lineSeparator());
            if (socketConnectPort != 0)
                sb.append("SocketConnectPort=").append(socketConnectPort).append(System.lineSeparator());
            if (senderCompID != null) sb.append("SenderCompID=").append(senderCompID).append(System.lineSeparator());
            if (senderSubID != null) sb.append("SenderSubID=").append(senderSubID).append(System.lineSeparator());
            if (senderLocationID != null)
                sb.append("senderLocationID=").append(senderLocationID).append(System.lineSeparator());
            if (targetCompID != null) sb.append("TargetCompID=").append(targetCompID).append(System.lineSeparator());
            if (targetSubID != null) sb.append("targetSubID=").append(targetSubID).append(System.lineSeparator());
            if (targetLocationID != null)
                sb.append("targetLocationID=").append(targetLocationID).append(System.lineSeparator());
            if (dataDictionary != null)
                sb.append("DataDictionary=").append(dataDictionary).append(System.lineSeparator());
        }

        return sb;
    }

    public void setSocketConnectPort(long socketConnectPort) {
        this.socketConnectPort = socketConnectPort;
    }

    public void setSenderCompID(String senderCompID) {
        this.senderCompID = senderCompID;
    }

    public void setDataDictionary(String dataDictionary) {
        this.dataDictionary = dataDictionary;
    }

    public String getSessionAlias() {
        return sessionAlias;
    }

    public void setSessionAlias(String sessionAlias) {
        this.sessionAlias = sessionAlias;
    }

    public String getSenderSubID() {
        return senderSubID;
    }

    public String getSessionQualifier() {
        return sessionQualifier;
    }

    public void setSessionQualifier(String sessionQualifier) {
        this.sessionQualifier = sessionQualifier;
    }

    public void setSenderSubID(String senderSubID) {
        this.senderSubID = senderSubID;
    }

    public String getSenderLocationID() {
        return senderLocationID;
    }

    public void setSenderLocationID(String senderLocationID) {
        this.senderLocationID = senderLocationID;
    }

    public String getTargetSubID() {
        return targetSubID;
    }

    public void setTargetSubID(String targetSubID) {
        this.targetSubID = targetSubID;
    }

    public String getTargetLocationID() {
        return targetLocationID;
    }

    public void setTargetLocationID(String targetLocationID) {
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

    public String getMmValidateUserDefinedFields() {
        return mmValidateUserDefinedFields;
    }

    public String getValidateIncomingMessage() {
        return validateIncomingMessage;
    }

    public String getRefreshOnLogon() {
        return refreshOnLogon;
    }

}
