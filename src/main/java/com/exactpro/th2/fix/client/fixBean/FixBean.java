package com.exactpro.th2.fix.client.fixBean;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static com.exactpro.th2.fix.client.util.FixBeanUtil.addToConfig;

public class FixBean extends BaseFixBean {

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
    protected String sessionAlias = null;


    public StringBuilder toConfig(String sectionName) {
        StringBuilder stringBuilder = super.toConfig(sectionName);
        addToConfig("BeginString", beginString, stringBuilder);
        addToConfig("SocketConnectHost", socketConnectHost, stringBuilder);
        addToConfig("SocketConnectPort", socketConnectPort, stringBuilder);
        addToConfig("SenderCompID", senderCompID, stringBuilder);
        addToConfig("SenderSubID", senderSubID, stringBuilder);
        addToConfig("SenderLocationID", senderLocationID, stringBuilder);
        addToConfig("TargetCompID", targetCompID, stringBuilder);
        addToConfig("TargetSubID", targetSubID, stringBuilder);
        addToConfig("TargetLocationID", targetLocationID, stringBuilder);
        addToConfig("DataDictionary", dataDictionary, stringBuilder);
        return stringBuilder;
    }

    public void setBeginString(String beginString) {
        if (beginString == null || beginString.isBlank()) {
            throw new IllegalArgumentException("beginString must not be null or blank.");
        }
        this.beginString = beginString;
    }

    public void setSocketConnectHost(String socketConnectHost) {
        if (socketConnectHost == null || socketConnectHost.isBlank()) {
            throw new IllegalArgumentException("socketConnectHost must not be null or blank.");
        }
        this.socketConnectHost = socketConnectHost;
    }

    public void setTargetCompID(String targetCompID) {
        if (targetCompID == null || targetCompID.isBlank()) {
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
        if (senderCompID == null || senderCompID.isBlank()) {
            throw new IllegalArgumentException("SenderCompId must not be null or blank.");
        }
        this.senderCompID = senderCompID;
    }

    public void setDataDictionary(String dataDictionary) {
        if (dataDictionary == null || dataDictionary.isBlank()) {
            throw new IllegalArgumentException("DataDictionary must not be null or blank.");
        }
        this.dataDictionary = dataDictionary;
    }

    public void setSessionAlias(String sessionAlias) {
        if (sessionAlias == null || sessionAlias.isBlank()) {
            throw new IllegalArgumentException("SessionAlias must not be null or blank.");
        }
        this.sessionAlias = sessionAlias;
    }

    public void setSenderSubID(String senderSubID) {
        if (senderSubID == null || senderSubID.isBlank()) {
            throw new IllegalArgumentException("SenderSubID must not be null or blank.");
        }
        this.senderSubID = senderSubID;
    }

    public void setSenderLocationID(String senderLocationID) {
        if (senderLocationID == null || senderLocationID.isBlank()) {
            throw new IllegalArgumentException("SenderLocationID must not be null or blank.");
        }
        this.senderLocationID = senderLocationID;
    }

    public void setTargetSubID(String targetSubID) {
        if (targetSubID == null || targetSubID.isBlank()) {
            throw new IllegalArgumentException("TargetSubID must not be null or blank.");
        }
        this.targetSubID = targetSubID;
    }

    public void setTargetLocationID(String targetLocationID) {
        if (targetLocationID == null || targetLocationID.isBlank()) {
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


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
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
                .append("SessionAlias", sessionAlias)
                .toString();
    }
}
