package com.exactpro.th2.fix.client.util;

import com.exactpro.th2.common.event.bean.IRow;
import com.exactpro.th2.common.event.bean.Table;
import com.exactpro.th2.common.event.bean.builder.TableBuilder;
import com.exactpro.th2.fix.client.Main.Settings;
import com.exactpro.th2.fix.client.exceptions.CreatingConfigFileException;
import com.exactpro.th2.fix.client.fixBean.FixBean;
import org.apache.commons.lang3.StringUtils;
import quickfix.SessionID;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FixBeanUtil {

    public static File createConfig(Settings settings) throws CreatingConfigFileException {

        StringBuilder sb = new StringBuilder();

        sb.append(settings.toConfig("default"));

        for (FixBean fixBean : settings.getSessionSettings()) {
            sb.append(fixBean.toConfig("session"));
        }

        File configFile;
        try {
            configFile = File.createTempFile("config", ".cfg");
            Files.writeString(Path.of(configFile.getAbsolutePath()), sb.toString());
            return configFile;
        } catch (IOException e) {
            throw new CreatingConfigFileException("Failed to create a config file.", e);
        }
    }

    public static <T> StringBuilder addToConfig(String tagName, T tagValue, StringBuilder sb) {

        if (tagName != null && tagValue != null) {
            sb.append(tagName)
                    .append("=")
                    .append(tagValue)
                    .append(System.lineSeparator());
        }
        return sb;
    }

    public static SessionID getSessionID(FixBean fixBean) {
        return new SessionID(fixBean.getBeginString(), fixBean.getSenderCompID(),
                fixBean.getSenderSubID(), fixBean.getSenderLocationID(), fixBean.getTargetCompID(),
                fixBean.getTargetSubID(), fixBean.getTargetLocationID(), "");
    }

    public static String requireNotNullOrBlank(String tagName, String tagValue) {
        if (StringUtils.isBlank(tagValue)) {
            throw new IllegalArgumentException(tagName + " must not be null or blank.");
        }
        return tagValue;
    }

    public static String convertFromBoolToYOrN(String tagName, String tagValue) {

        if (tagValue.equals("true")) {
            tagValue = "Y";
            return tagValue;
        } else if (tagValue.equals("false")) {
            tagValue = "N";
            return tagValue;
        } else {
            throw new IllegalArgumentException(tagName + " must be \"true\" or \"false\".");
        }
    }

    public static FixBean getSessionSettingsBySessionAlias(List<FixBean> sessionSettings, String sessionAlias) {
        for (FixBean fixBean : sessionSettings) {
            if (sessionAlias.equals(fixBean.getSessionAlias())) {
                return fixBean;
            }
        }
        return null;
    }

    public static Table getSessionTable(List<FixBean> sessionSettings) {

        TableBuilder<Row> tableBuilder = new TableBuilder<>();

        String dataDictionary;
        for (FixBean settings : sessionSettings) {
            dataDictionary = settings.getDataDictionary() != null ? settings.getDataDictionary().getFileName().toString()
                    : (settings.getAppDataDictionary().getFileName().toString() + ", " + settings.getTransportDataDictionary().getFileName());

            tableBuilder.row(new Row(settings.getSessionAlias(),
                    settings.getSenderCompID(),
                    settings.getTargetCompID(),
                    settings.getSocketConnectHost(),
                    Long.toString(settings.getSocketConnectPort()),
                    settings.getUsername(),
                    dataDictionary));
        }
        return tableBuilder.build();
    }

    private static class Row implements IRow {
        String alias;
        String senderCompID;
        String targetCompID;
        String socketConnectHost;
        String socketConnectPort;
        String username;
        String dataDictionary;

        public Row(String alias, String senderCompID, String targetCompID, String socketConnectHost, String socketConnectPort, String username, String dataDictionary) {
            this.alias = alias;
            this.senderCompID = senderCompID;
            this.targetCompID = targetCompID;
            this.socketConnectHost = socketConnectHost;
            this.socketConnectPort = socketConnectPort;
            this.username = username;
            this.dataDictionary = dataDictionary;
        }

        public String getAlias() {
            return alias;
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

        public String getSocketConnectPort() {
            return socketConnectPort;
        }

        public String getUsername() {
            return username;
        }

        public String getDataDictionary() {
            return dataDictionary;
        }
    }
}
