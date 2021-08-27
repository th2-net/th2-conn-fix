package com.exactpro.th2.fix.client.util;

import com.exactpro.th2.fix.client.fixBean.FixBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class FixBeanUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FixBeanUtil.class);

    public static File createConfig(List<FixBean> fixBeans) {
        StringBuilder sb = new StringBuilder();

        sb.append(fixBeans.get(0).toConfig("default"));

        for (FixBean fixBean : fixBeans) {
            sb.append(fixBean.toConfig("session"));
        }
        File configFile = null;
        try {
            configFile = File.createTempFile("config", ".cfg");
            Files.writeString(Path.of(configFile.getAbsolutePath()), sb.toString(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            LOGGER.error("Failed to create config file", e);
            System.exit(1);
        }
        return configFile;
    }
}
