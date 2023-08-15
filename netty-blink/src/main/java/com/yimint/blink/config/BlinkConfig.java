package com.yimint.blink.config;

import com.yimint.blink.exception.BlinkException;
import com.yimint.blink.exception.StatusEnum;

/**
 * @Auther：yimint
 * @Date: 2023-08-14 16:33
 * @Description
 */
public final class BlinkConfig {
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
            throw new BlinkException(StatusEnum.CLASS_NOT_NULL);
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
}
