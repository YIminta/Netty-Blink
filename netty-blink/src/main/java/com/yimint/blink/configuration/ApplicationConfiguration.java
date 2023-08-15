package com.yimint.blink.configuration;

import java.util.Properties;

/**
 * @Auther：yimint
 * @Date: 2023-08-15 9:14
 * @Description
 */
public class ApplicationConfiguration {

    private static Properties properties;

    public static void setProperties(Properties properties) {
        ApplicationConfiguration.properties = properties;
    }

    public static String get(String key) {
        return properties.get(key) == null ? null : properties.get(key).toString();
    }
}
