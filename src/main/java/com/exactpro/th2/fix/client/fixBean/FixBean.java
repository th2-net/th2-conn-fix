package com.exactpro.th2.fix.client.fixBean;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.nio.file.Path;

import static com.exactpro.th2.fix.client.util.FixBeanUtil.addToConfig;
import static com.exactpro.th2.fix.client.util.FixBeanUtil.requireNotNullOrBlank;

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
    protected Path dataDictionary = null;
    protected Path appDataDictionary = null;
    protected Path transportDataDictionary = null;
    protected String defaultApplVerID = "9";
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
        addToConfig("AppDataDictionary", appDataDictionary, stringBuilder);
        addToConfig("TransportDataDictionary", transportDataDictionary, stringBuilder);
        addToConfig("DefaultApplVerID", defaultApplVerID, stringBuilder);
        return stringBuilder;
    }

    public void setBeginString(String beginString) {
        this.beginString = requireNotNullOrBlank("BeginString", beginString);
    }

    public void setSocketConnectHost(String socketConnectHost) {
        this.socketConnectHost = requireNotNullOrBlank("SocketConnectHost", socketConnectHost);
    }

    public void setTargetCompID(String targetCompID) {
        this.targetCompID = requireNotNullOrBlank("TargetCompID", targetCompID);
    }

    public void setSocketConnectPort(long socketConnectPort) {
        if (socketConnectPort < 1024 || socketConnectPort > 65535) {
            throw new IllegalArgumentException("SocketConnectPort must be in range from 1024 to 65535.");
        }
        this.socketConnectPort = socketConnectPort;
    }

    public void setSenderCompID(String senderCompID) {
        this.senderCompID = requireNotNullOrBlank("SenderCompID", senderCompID);
    }

    public void setDataDictionary(String dataDictionary) {
        this.dataDictionary = Path.of(requireNotNullOrBlank("DataDictionary", dataDictionary));
    }

    public void setSessionAlias(String sessionAlias) {
        this.sessionAlias = requireNotNullOrBlank("SessionAlias", sessionAlias);
    }

    public void setSenderSubID(String senderSubID) {
        this.senderSubID = requireNotNullOrBlank("SenderSubID", senderSubID);
    }

    public void setSenderLocationID(String senderLocationID) {
        this.senderLocationID = requireNotNullOrBlank("SenderLocationID", senderLocationID);
    }

    public void setTargetSubID(String targetSubID) {
        this.targetSubID = requireNotNullOrBlank("TargetSubID", targetSubID);
    }

    public void setTargetLocationID(String targetLocationID) {
        this.targetLocationID = requireNotNullOrBlank("TargetLocationID", targetLocationID);
    }

    public void setDefaultApplVerID(String defaultApplVerID) {
        this.defaultApplVerID = requireNotNullOrBlank("DefaultApplVerID", defaultApplVerID);
    }

    public void setAppDataDictionary(String appDataDictionary) {
        this.appDataDictionary = Path.of(requireNotNullOrBlank("AppDataDictionary", appDataDictionary));
    }

    public void setTransportDataDictionary(String transportDataDictionary) {
        this.transportDataDictionary = Path.of(requireNotNullOrBlank("TransportDataDictionary", transportDataDictionary));
    }

    public Path getTransportDataDictionary() {
        return transportDataDictionary;
    }

    public String getDefaultApplVerID() {
        return defaultApplVerID;
    }

    public String getSessionAlias() {
        return sessionAlias;
    }

    public Path getAppDataDictionary() {
        return appDataDictionary;
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

    public Path getDataDictionary() {
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
                .append("AppDataDictionary", appDataDictionary)
                .append("TransportDataDictionary", transportDataDictionary)
                .append("DefaultApplVerID", defaultApplVerID)
                .append("SessionAlias", sessionAlias)
                .toString();
    }
}
