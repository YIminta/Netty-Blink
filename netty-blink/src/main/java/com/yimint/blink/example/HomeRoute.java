package com.yimint.blink.example;

import com.yimint.blink.annotation.BlinkController;
import com.yimint.blink.annotation.BlinkMethod;

/**
 * @Auther：yimint
 * @Date: 2023-08-11 15:26
 * @Description
 */
@BlinkController("/home")
public class HomeRoute {

    @BlinkMethod()
    public String home(String name) {
        return "Welcome come home NettyHttp：" + name;
    }
}
