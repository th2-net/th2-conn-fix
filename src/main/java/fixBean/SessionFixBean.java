package fixBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.field.BeginString;
import quickfix.field.SenderCompID;
import quickfix.field.TargetCompID;

import java.io.FileOutputStream;
import java.io.IOException;

public class SessionFixBean extends BaseFixBean {
    private static final Logger log = LoggerFactory.getLogger(SessionFixBean.class);

    private BeginString beginString = new BeginString("FIX.4.2");
    private String socketConnectHost = "localhost";
    private long socketConnectPort = 9877;
    private TargetCompID targetCompID = new TargetCompID("server");
    private SenderCompID senderCompID = new SenderCompID("client");
    private String sessionAlias = "Client";
    private String acceptorTemplate = "Y";


    public SessionFixBean(BeginString beginString, long socketAcceptPort, TargetCompID targetCompID, SenderCompID senderCompID, String acceptorTemplate) {
        this.beginString = beginString;
        this.socketConnectPort = socketAcceptPort;
        this.targetCompID = targetCompID;
        this.senderCompID = senderCompID;
        this.acceptorTemplate = acceptorTemplate;
    }

    public SessionFixBean(){
    }

    public String toConfig(String sectionName){
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
            if (acceptorTemplate != null) sb.append("AcceptorTemplate=").append(acceptorTemplate).append("\n");
        }//I'm so sorry about your pain ;(

        return sb.toString();
    }

    public byte[] toByteArray(){
        StringBuilder sb = new StringBuilder();
        sb.append(toConfig("default")).append(toConfig("session"));
        return sb.toString().getBytes();
    }

    public void createConfig(){

        try(FileOutputStream fos = new FileOutputStream("src/main/resources/acceptor/acceptor.cfg")) {

            fos.write(toByteArray());

        }catch (IOException e){
            log.error("Error occurs when creating config",e);
        }
    }

    public String getSocketConnectHost() {
        return socketConnectHost;
    }

    public void setSocketConnectHost(String socketConnectHost) {
        this.socketConnectHost = socketConnectHost;
    }

    public String getSessionAlias() {
        return sessionAlias;
    }

    public void setSessionAlias(String sessionAlias) {
        this.sessionAlias = sessionAlias;
    }

    public SenderCompID getSenderCompID() {
        return senderCompID;
    }


    public void setSenderCompID(SenderCompID senderCompID) {
        this.senderCompID = senderCompID;
    }

    public BeginString getBeginString() {
        return beginString;
    }

    public void setBeginString(BeginString beginString) {
        this.beginString = beginString;
    }

    public long getSocketConnectPort() {
        return socketConnectPort;
    }

    public void setSocketConnectPort(long socketAcceptPort) {
        this.socketConnectPort = socketAcceptPort;
    }


    public TargetCompID getTargetCompID() {
        return targetCompID;
    }

    public void setTargetCompID(TargetCompID targetCompID) {
        this.targetCompID = targetCompID;
    }

    public String getAcceptorTemplate() {
        return acceptorTemplate;
    }

    public void setAcceptorTemplate(String acceptorTemplate) {
        this.acceptorTemplate = acceptorTemplate;
    }
}
