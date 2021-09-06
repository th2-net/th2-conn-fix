package com.exactpro.th2.fix.client.fixBean;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.regex.Pattern;

import static com.exactpro.th2.fix.client.util.FixBeanUtil.addToConfig;

public class BaseFixBean {

    private static final Pattern YES_OR_NO = Pattern.compile("[YN]?");

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

        return sb;
    }

    public void setFileStorePath(String fileStorePath) {
        if (fileStorePath == null || fileStorePath.isBlank()) {
            throw new IllegalArgumentException("fileStorePath must not be null or blank.");
        }
        this.fileStorePath = fileStorePath;
    }

    public void setFileLogPath(String fileLogPath) {
        if (fileLogPath == null || fileLogPath.isBlank()) {
            throw new IllegalArgumentException("FileLogPath must not be null or blank.");
        }
        this.fileLogPath = fileLogPath;
    }

    public void setReconnectInterval(long reconnectInterval) {
        if (reconnectInterval < 30) { //todo which value is valid?
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
        if (!YES_OR_NO.matcher(validateUserDefinedFields).matches()) {
            throw new IllegalArgumentException("validateUserDefinedFields must be \"Y\" or \"N\".");
        }
        this.validateUserDefinedFields = validateUserDefinedFields;
    }

    public void setValidateIncomingMessage(String validateIncomingMessage) {
        if (!YES_OR_NO.matcher(validateIncomingMessage).matches()) {
            throw new IllegalArgumentException("validateIncomingMessage must be \"Y\" or \"N\".");
        }
        this.validateIncomingMessage = validateIncomingMessage;
    }

    public void setRefreshOnLogon(String refreshOnLogon) {
        if (!YES_OR_NO.matcher(refreshOnLogon).matches()) {
            throw new IllegalArgumentException("refreshOnLogon must be \"Y\" or \"N\".");
        }
        this.refreshOnLogon = refreshOnLogon;
    }

    public void setNonStopSession(String nonStopSession) {
        if (!YES_OR_NO.matcher(nonStopSession).matches()) {
            throw new IllegalArgumentException("nonStopSession must be \"Y\" or \"N\".");
        }
        this.nonStopSession = nonStopSession;
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
                .toString();
    }
}