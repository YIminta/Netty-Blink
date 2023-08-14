package com.yimint.blink.bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther：yimint
 * @Date: 2023-08-14 17:15
 * @Description
 */
public class RouteBeanManager {
    private static final Map<String,Object> BEANS = new ConcurrentHashMap<>(16) ;

    public static void initBean(Object object) {
        BEANS.put(object.getClass().getName(),object) ;
    }

    public static Object getBean(String name) {
        return BEANS.get(name);
    }

    public static  <T> T getBean(Class<T> clazz) {
        return (T) getBean(clazz.getName());
    }

}
