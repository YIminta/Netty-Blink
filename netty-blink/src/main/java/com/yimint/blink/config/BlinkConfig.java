package com.yimint.blink.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * @Auther：yimint
 * @Date: 2023-08-14 16:33
 * @Description
 */
public final class BlinkConfig {
    public static final String PROPERTIES_NAME = "application.properties";
    public static final Integer BLINK_PORT = 54123;
    private Properties properties;
    private static BlinkConfig config;

    public static BlinkConfig getInstance() {
        if (config == null) {
            config = new BlinkConfig();
        }
        return config;
    }
    private String rootPackageName;

    private String rootPath;

    private Integer port = 54123;


    public String getRootPackageName() {
        return rootPackageName;
    }

    public void setRootPackageName(Class<?> clazz) {
        if (clazz.getPackage() == null) {

        }
        this.rootPackageName = clazz.getPackage().getName();
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
