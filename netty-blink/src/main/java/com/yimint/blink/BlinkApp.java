package com.yimint.blink;

import com.yimint.blink.config.BlinkInitialize;
import com.yimint.blink.server.HttpServer;

/**
 * @Auther：yimint
 * @Date: 2023-08-11 11:09
 * @Description
 */
public class BlinkApp {
    public static void main(String[] args) throws Exception {

        BlinkInitialize.init(BlinkApp.class);

        HttpServer.start();
    }
}
