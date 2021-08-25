package com.exactpro.th2.fix.client.service;

import com.exactpro.th2.fix.client.fixBean.FixBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FixBeanService extends FixBean{

    private static final Logger LOGGER = LoggerFactory.getLogger(FixBeanService.class);

    public File createConfig(List<FixBean> fixBeans){
        StringBuilder sb = new StringBuilder();

        sb.append(fixBeans.get(0).toConfig("default"));

        for (FixBean fixBean: fixBeans){
            sb.append(fixBean.toConfig("session"));
        }
        File configFile = null;
        try {
            configFile = File.createTempFile("config", ".cfg");
            OutputStream outputStream = Files.newOutputStream(Path.of(configFile.getAbsolutePath()));
            outputStream.write(sb.toString().getBytes());
        }catch (IOException e){
            LOGGER.error("Failed to create config file", e);
        }
        return configFile;
    }

    public List<String> getSessionAliases(List<FixBean> fixBeans){
        List<String> sessionAliases = new ArrayList<>();
        for (FixBean fixBean: fixBeans){
            sessionAliases.add(fixBean.getSessionAlias());
        }
        return sessionAliases;
    }
}
