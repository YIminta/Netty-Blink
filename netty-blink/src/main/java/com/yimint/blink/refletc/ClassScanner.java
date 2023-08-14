package com.yimint.blink.refletc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import com.yimint.blink.annotation.BlinkController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther：yimint
 * @Date: 2023-08-11 10:58
 * @Description
 */
public class ClassScanner {

    private final static Logger LOGGER = LoggerFactory.getLogger(ClassScanner.class);


    private static Map<String, Class<?>> routeMap = null;
    private static Map<String, Class<?>> routeBeanMap = null;
    private static Set<Class<?>> classes = null;

    public static Set<Class<?>> getClasses(String packageName) {
        if (classes == null) {
            classes = new HashSet<>(32);

            scanClasses(packageName, classes);
        }
        return classes;
    }

    public static Map<String, Class<?>> getBlinkRouteBean() {


        return routeMap;
    }

    public static void scanClasses(String packageName, Set<Class<?>> classes) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = null;
        try {
            resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());
                if (!directory.exists() || !directory.isDirectory()) {
                    return;
                }
                scanClasses(packageName, directory, classes);
            }
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("ClassNotFoundException", e);
        }
    }

    private static void scanClasses(String packageName, File directory, Set<Class<?>> classes) throws ClassNotFoundException {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                scanClasses(packageName + "." + file.getName(), file, classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                Class<?> clazz = Class.forName(className);
                classes.add(clazz);
            }
        }
    }

    public static Map<String, Class<?>> getRouteBeanMap(String packageName) throws Exception {
        if (routeBeanMap == null) {
            Set<Class<?>> clsList = getClasses(packageName);

            if (clsList == null || clsList.isEmpty()) {
                return routeBeanMap;
            }
            routeBeanMap = new HashMap<>(16);
            for (Class<?> cls : clsList) {
                BlinkController controller = cls.getAnnotation(BlinkController.class);
                if (controller != null){
                    routeBeanMap.put(controller.value() == null ? cls.getName() : controller.value(), cls);
                }
            }
        }
        return routeBeanMap;
    }
}