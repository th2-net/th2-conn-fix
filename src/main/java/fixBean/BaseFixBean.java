package fixBean;


import quickfix.field.HeartBtInt;

public class BaseFixBean {

    protected String applicationID = "client";
    protected String fileStorePath = "storage/messages/";
    protected String fileLogPath = "outgoing";
    protected String connectionType = "initiator";
    protected long reconnectInterval = 60;
//    protected String startTime = "00:01:00 Europe/Bucharest";
//    protected String endTime = "23:59:00 Europe/Bucharest";
    protected HeartBtInt heartBtInt = new HeartBtInt(30);
    protected String useDataDictionary = "Y";
    //    private DataDictionary dataDictionary = new DataDictionary(FIX42.xml);
    protected String mmValidateUserDefinedFields = "N";
    protected String validateIncomingMessage = "N";
    protected String refreshOnLogon = "Y";
    protected String NonStopSession = "Y";

    public BaseFixBean() {
    }

    public BaseFixBean(String applicationID, String fileStorePath, String fileLogPath, String connectionType, long reconnectInterval, HeartBtInt heartBtInt, String useDataDictionary, String mmValidateUserDefinedFields, String validateIncomingMessage, String refreshOnLogon, String nonStopSession) {
        this.applicationID = applicationID;
        this.fileStorePath = fileStorePath;
        this.fileLogPath = fileLogPath;
        this.connectionType = connectionType;
        this.reconnectInterval = reconnectInterval;
        this.heartBtInt = heartBtInt;
        this.useDataDictionary = useDataDictionary;
        this.mmValidateUserDefinedFields = mmValidateUserDefinedFields;
        this.validateIncomingMessage = validateIncomingMessage;
        this.refreshOnLogon = refreshOnLogon;
        NonStopSession = nonStopSession;
    }

    public String getNonStopSession() {
        return NonStopSession;
    }

    public void setNonStopSession(String nonStopSession) {
        NonStopSession = nonStopSession;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public String getFileStorePath() {
        return fileStorePath;
    }

    public void setFileStorePath(String fileStorePath) {
        this.fileStorePath = fileStorePath;
    }

    public String getFileLogPath() {
        return fileLogPath;
    }

    public void setFileLogPath(String fileLogPath) {
        this.fileLogPath = fileLogPath;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public long getReconnectInterval() {
        return reconnectInterval;
    }

    public void setReconnectInterval(long reconnectInterval) {
        this.reconnectInterval = reconnectInterval;
    }

    public HeartBtInt getHeartBtInt() {
        return heartBtInt;
    }

    public void setHeartBtInt(HeartBtInt heartBtInt) {
        this.heartBtInt = heartBtInt;
    }

    public String getUseDataDictionary() {
        return useDataDictionary;
    }

    public void setUseDataDictionary(String useDataDictionary) {
        this.useDataDictionary = useDataDictionary;
    }

    public String getMmValidateUserDefinedFields() {
        return mmValidateUserDefinedFields;
    }

    public void setMmValidateUserDefinedFields(String mmValidateUserDefinedFields) {
        this.mmValidateUserDefinedFields = mmValidateUserDefinedFields;
    }

    public String getValidateIncomingMessage() {
        return validateIncomingMessage;
    }

    public void setValidateIncomingMessage(String validateIncomingMessage) {
        this.validateIncomingMessage = validateIncomingMessage;
    }

    public String getRefreshOnLogon() {
        return refreshOnLogon;
    }

    public void setRefreshOnLogon(String refreshOnLogon) {
        this.refreshOnLogon = refreshOnLogon;
    }
}
