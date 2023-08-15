package com.yimint.blink.handler;

import com.yimint.blink.route.RouterHandler;
import com.yimint.blink.route.RouterScanner;
import com.yimint.netty.common.util.HttpProtocolHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.lang.reflect.Method;
import java.net.URLDecoder;

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
            HttpProtocolHelper.sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        String uri = request.uri();
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(URLDecoder.decode(uri, "UTF-8"));

        Method method = RouterScanner.getInstance().getRouteMethod(queryStringDecoder);
        String result = RouterHandler.getInstance().invoke(method, queryStringDecoder);

        HttpProtocolHelper.sendJsonContent(ctx, result);
    }
}
