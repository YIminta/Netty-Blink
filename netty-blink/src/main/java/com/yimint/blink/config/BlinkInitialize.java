package com.yimint.blink.config;

import com.yimint.blink.bean.RouteBeanManager;
import com.yimint.blink.refletc.ClassScanner;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;

import static com.yimint.blink.config.BlinkConfig.BLINK_PORT;
import static com.yimint.blink.config.BlinkConfig.PROPERTIES_NAME;

/**
 * @Auther：yimint
 * @Date: 2023-08-14 16:52
 * @Description
 */
public class BlinkInitialize {

    public static void init(Class<?> clazz) throws Exception {

        initConfiguration(clazz);

        initBean();
    }

    private static void initConfiguration(Class<?> clazz) throws Exception {
        BlinkConfig.getInstance().setRootPackageName(clazz);
        String property = System.getProperty(PROPERTIES_NAME);
        if (property == null) {
            return;
        }
        InputStream stream = Files.newInputStream(new File(property).toPath());
        Properties properties = new Properties();
        properties.load(stream);

        BlinkConfig.getInstance().setProperties(properties);

        String port = properties.get(BLINK_PORT).toString();
        if (port != null) {
            BlinkConfig.getInstance().setPort(Integer.parseInt(port));
        }
    }

    public static void initBean() throws Exception {
        Map<String, Class<?>> routeBeanMap = ClassScanner.getRouteBeanMap(BlinkConfig.getInstance().getRootPackageName());

        for (Map.Entry<String, Class<?>> classEntry : routeBeanMap.entrySet()) {
            Object instance = classEntry.getValue().newInstance();
            RouteBeanManager.initBean(instance);
        }
    }
}
