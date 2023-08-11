package com.yimint.blink.annotation;

import java.lang.annotation.*;

/**
 * @Auther：yimint
 * @Date: 2023-08-11 13:56
 * @Description
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BlinkMethod {

    String value() default "";
}
