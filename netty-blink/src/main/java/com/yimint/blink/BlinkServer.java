package com.yimint.blink;

import com.yimint.blink.config.BlinkInitialize;
import com.yimint.blink.server.HttpServer;

/**
 * @Auther：yimint
 * @Date: 2023-08-11 11:09
 * @Description
 */
public class BlinkServer {
    public static void start(Class<?> clazz) throws Exception {

        BlinkInitialize.init(clazz);

        HttpServer.startServer();
    }
}
