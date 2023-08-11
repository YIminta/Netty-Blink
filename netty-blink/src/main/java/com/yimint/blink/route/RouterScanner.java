package com.yimint.blink.route;

import com.yimint.blink.annotation.BlinkController;
import com.yimint.blink.annotation.BlinkMethod;
import com.yimint.blink.refletc.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Auther：yimint
 * @Date: 2023-08-11 13:38
 * @Description
 */
public class RouterScanner {

    private final static Logger LOGGER = LoggerFactory.getLogger(RouterScanner.class);

    private static Map<String, Method> routes = null;

    private static volatile RouterScanner routerScanner;

    public static RouterScanner getInstance() {
        if (routerScanner == null) {
            synchronized (RouterScanner.class) {
                if (routerScanner == null) {
                    routerScanner = new RouterScanner();
                }
            }
        }
        return routerScanner;
    }

    public Method getRouteMethod(String url) throws Exception {
        if (routes == null) {
            routes = new HashMap<>();
            initRouteMethod("com.yimint.blink");
        }
        Method method = routes.get(url);
        if (method == null) {
            throw new Exception("url not match method");
        }
        return method;
    }

    private void initRouteMethod(String packageName) {
        Set<Class<?>> classes = ClassScanner.getClasses(packageName);

        for (Class<?> aClass : classes) {

            Method[] methods = aClass.getMethods();

            for (Method method : methods) {
                BlinkMethod blinkMethod = method.getAnnotation(BlinkMethod.class);
                if (blinkMethod == null) {
                    continue;
                }
                BlinkController blinkController = aClass.getAnnotation(BlinkController.class);
                String key = blinkController.value() + (!blinkMethod.value().equals("") ? "/" + blinkMethod.value() : "");
                routes.put(key, method);
            }
        }
    }
}
