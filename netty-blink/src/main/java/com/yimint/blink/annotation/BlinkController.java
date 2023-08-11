package com.yimint.blink.annotation;

import java.lang.annotation.*;

/**
 * @Auther：yimint
 * @Date: 2023-08-11 10:56
 * @Description
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BlinkController {

    String value() default "";
}
