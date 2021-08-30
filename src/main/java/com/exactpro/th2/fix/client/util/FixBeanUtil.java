package com.exactpro.th2.fix.client.util;

import com.exactpro.th2.fix.client.Main;
import com.exactpro.th2.fix.client.fixBean.FixBean;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FixBeanUtil {

    public static File createConfig(Main.Settings settings) throws IOException {

        StringBuilder sb = new StringBuilder();

        sb.append(settings.toConfig("default"));

        for (FixBean fixBean : settings.getSessionsSettings()) {
            sb.append(fixBean.toConfig("session"));
        }

        File configFile;
        try {
            configFile = File.createTempFile("config", ".cfg");
            Files.writeString(Path.of(configFile.getAbsolutePath()), sb.toString());
        } catch (IOException e) {
            throw new IOException("Failed to create config file.", e);
        }

        return configFile;
    }
}
