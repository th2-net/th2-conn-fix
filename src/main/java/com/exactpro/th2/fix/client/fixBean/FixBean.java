package com.exactpro.th2.fix.client.fixBean;


import quickfix.field.BeginString;
import quickfix.field.HeartBtInt;
import quickfix.field.SenderCompID;
import quickfix.field.TargetCompID;

public class FixBean {

    protected String applicationID = "client";
    protected String fileStorePath = "storage/messages/";
    protected String fileLogPath = "outgoing";
    protected String connectionType = "initiator";
    protected long reconnectInterval = 60;
    protected HeartBtInt heartBtInt = new HeartBtInt(30);
    protected String useDataDictionary = "Y";
    protected String mmValidateUserDefinedFields = "N";
    protected String validateIncomingMessage = "N";
    protected String refreshOnLogon = "Y";
    protected String NonStopSession = "Y";

    protected BeginString beginString = new BeginString("FIX.4.2");
    protected String socketConnectHost = "localhost";
    protected long socketConnectPort = 9877;
    protected SenderCompID senderCompID = new SenderCompID("client");
    protected TargetCompID targetCompID = new TargetCompID("server");
    protected String sessionAlias = "FIX.4.2:client->server";

    public FixBean() {
    }

    public StringBuilder toConfig(String sectionName){ //todo mb getting fixBean as argument to fixBean was POJO?
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(sectionName).append("]").append("\n");
        if(sectionName.equals("default")) {
            sb.append("ApplicationID=").append(applicationID).append("\n");
            sb.append("FileStorePath=").append(fileStorePath).append("\n");
            sb.append("FileLogPath=").append(fileLogPath).append("\n");
            sb.append("ConnectionType=").append(connectionType).append("\n");
            sb.append("ReconnectInterval=").append(reconnectInterval).append("\n");
            sb.append("NonStopSession=").append("Y").append("\n");
            sb.append("HeartBtInt=").append(heartBtInt.getValue()).append("\n");
            sb.append("UseDataDictionary=").append(useDataDictionary).append("\n");
            sb.append("ValidateUserDefinedFields=").append(mmValidateUserDefinedFields).append("\n");
            sb.append("ValidateIncomingMessage=").append(validateIncomingMessage).append("\n");
            sb.append("RefreshOnLogon=").append(refreshOnLogon).append("\n");
        }
        if(sectionName.equals("session")) {
            if (beginString != null) sb.append("BeginString=").append(beginString.getValue()).append("\n");
            if (socketConnectHost != null) sb.append("SocketConnectHost=").append(socketConnectHost).append("\n");
            if (socketConnectPort != 0) sb.append("SocketConnectPort=").append(socketConnectPort).append("\n");
            if (senderCompID != null) sb.append("SenderCompID=").append(senderCompID.getValue()).append("\n");
            if (targetCompID != null) sb.append("TargetCompID=").append(targetCompID.getValue()).append("\n");
            if (sessionAlias != null) sb.append("SessionAlias=").append(sessionAlias).append("\n");
        }

        return sb;
    }

    public void setSocketConnectPort(long socketConnectPort) {
        this.socketConnectPort = socketConnectPort;
    }

    public void setSenderCompID(SenderCompID senderCompID) {//todo delete
        this.senderCompID = senderCompID;
    }

    public void setSessionAlias(String sessionAlias) {
        this.sessionAlias = sessionAlias;
    }

    public BeginString getBeginString() {
        return beginString;
    }

    public String getSocketConnectHost() {
        return socketConnectHost;
    }

    public long getSocketConnectPort() {
        return socketConnectPort;
    }

    public TargetCompID getTargetCompID() {
        return targetCompID;
    }

    public SenderCompID getSenderCompID() {
        return senderCompID;
    }

    public String getSessionAlias() {
        return sessionAlias;
    }

    public String getNonStopSession() {
        return NonStopSession;
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

    public HeartBtInt getHeartBtInt() {
        return heartBtInt;
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
