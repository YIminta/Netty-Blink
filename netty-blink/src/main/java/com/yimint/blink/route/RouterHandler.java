package com.yimint.blink.route;

import io.netty.handler.codec.http.QueryStringDecoder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @Auther：yimint
 * @Date: 2023-08-14 11:47
 * @Description
 */
public class RouterHandler {
    private static final Map<Class<?>, Function<String, Object>> PARAM_TYPE_HANDLERS = new HashMap<>();
    static {
        PARAM_TYPE_HANDLERS.put(String.class, String::valueOf);
        PARAM_TYPE_HANDLERS.put(int.class, Integer::parseInt);
        PARAM_TYPE_HANDLERS.put(Integer.class, Integer::parseInt);
        PARAM_TYPE_HANDLERS.put(boolean.class, Boolean::parseBoolean);
        PARAM_TYPE_HANDLERS.put(Boolean.class, Boolean::parseBoolean);
        PARAM_TYPE_HANDLERS.put(double.class, Double::parseDouble);
        PARAM_TYPE_HANDLERS.put(Double.class, Double::parseDouble);
    }
    private static volatile RouterHandler routerHandler;

    public static RouterHandler getInstance() {
        if (routerHandler == null) {
            synchronized (RouterHandler.class) {
                if (routerHandler == null) {
                    routerHandler = new RouterHandler();
                }
            }
        }
        return routerHandler;
    }


    public String invoke(Method method, QueryStringDecoder queryStringDecoder) throws Exception {
        if (method == null) {
            return null;
        }
        Object[] object = parseRouteParameter(method, queryStringDecoder);
        String name = method.getDeclaringClass().getName();
        Object instance = Class.forName(name).newInstance();
        Object result;
        if (object == null) {
            result = method.invoke(instance);
        } else {
            result = method.invoke(instance, object);
        }
        instance = null;
        return result.toString();
    }

    private Object[] parseRouteParameter(Method method, QueryStringDecoder queryStringDecoder) throws Exception {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) {
            return null;
        }
        Object[] instances = new Object[parameterTypes.length];
        Map<String, List<String>> parameters = queryStringDecoder.parameters();
        for (int i = 0; i < instances.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (PARAM_TYPE_HANDLERS.containsKey(parameterType)) {
                String paramName = parameters.keySet().iterator().next();
                String paramValue = parameters.get(paramName).get(0);
                instances[i] = PARAM_TYPE_HANDLERS.get(parameterType).apply(paramValue);
            } else {
                Object instance = parameterType.newInstance();
                for (Map.Entry<String, List<String>> param : parameters.entrySet()) {
                    Field field = parameterType.getDeclaredField(param.getKey());
                    field.setAccessible(true);
                    field.set(instance, parseFieldValue(field, param.getValue().get(0)));
                }
                instances[i] = instance;
            }
        }
        return instances;
    }

    private Object parseFieldValue(Field field, String value) {
        if (value == null) {
            return null;
        }

        Class<?> type = field.getType();
        if ("".equals(value)) {
            boolean base = type.equals(int.class) || type.equals(double.class) ||
                    type.equals(short.class) || type.equals(long.class) ||
                    type.equals(byte.class) || type.equals(float.class);
            if (base) {
                return 0;
            }
        }
        if (type.equals(int.class) || type.equals(Integer.class)) {
            return Integer.parseInt(value);
        } else if (type.equals(String.class)) {
            return value;
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            return Double.parseDouble(value);
        } else if (type.equals(Float.class) || type.equals(float.class)) {
            return Float.parseFloat(value);
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return Long.parseLong(value);
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return Boolean.parseBoolean(value);
        } else if (type.equals(Short.class) || type.equals(short.class)) {
            return Short.parseShort(value);
        } else if (type.equals(Byte.class) || type.equals(byte.class)) {
            return Byte.parseByte(value);
        } else if (type.equals(BigDecimal.class)) {
            return new BigDecimal(value);
        }

        return null;
    }
}
