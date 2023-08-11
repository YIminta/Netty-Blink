package com.yimint.blink.Handler;

import com.yimint.blink.route.RouterScanner;
import com.yimint.netty.common.util.HttpProtocolHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.lang.reflect.Method;
import java.net.URLDecoder;

import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;

/**
 * @Auther：yimint
 * @Date: 2023-08-11 15:20
 * @Description
 */
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            HttpProtocolHelper.sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        String uri = request.uri();
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(URLDecoder.decode(uri, "UTF-8"));

        String path = queryStringDecoder.path();
        Method routeMethod = RouterScanner.getInstance().getRouteMethod(path);
        String name = routeMethod.getDeclaringClass().getName();
        Object instance = Class.forName(name).newInstance();
        Object invoke = routeMethod.invoke(instance);
        HttpProtocolHelper.sendJsonContent(ctx, invoke.toString());
    }
}
