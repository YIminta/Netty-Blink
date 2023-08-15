package com.yimint.blink.server;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.yimint.blink.handler.HttpHandler;
import com.yimint.blink.config.BlinkConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther：yimint
 * @Date: 2023-08-11 15:15
 * @Description
 */
public class HttpServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);
    public static final int SERVER_PORT = BlinkConfig.getInstance().getPort();

    public static void startServer() {
        ServerBootstrap b = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //请求解码器和响应编码器,等价于下面两行
                            // pipeline.addLast(new HttpServerCodec());
                            //请求解码器
                            pipeline.addLast(new HttpRequestDecoder());
                            //响应编码器
                            pipeline.addLast(new HttpResponseEncoder());
                            // HttpObjectAggregator 将HTTP消息的多个部分合成一条完整的HTTP消息
                            // HttpObjectAggregator 用于解析Http完整请求
                            // 把多个消息转换为一个单一的完全FullHttpRequest或是FullHttpResponse，
                            // 原因是HTTP解码器会在解析每个HTTP消息中生成多个消息对象
                            // 如 HttpRequest/HttpResponse,HttpContent,LastHttpContent
                            pipeline.addLast(new HttpObjectAggregator(65535));
                            pipeline.addLast(new ChunkedWriteHandler());
                            // 自定义的业务handler
                            pipeline.addLast(new HttpHandler());
                        }
                    });
            Channel ch = b.bind(SERVER_PORT).sync().channel();
            LOGGER.info("HTTP ECHO 服务已经启动 http://{}:{}/", "127.0.0.1", SERVER_PORT);
            // 等待服务端监听到端口关闭
            ch.closeFuture().sync();
        } catch (Exception ex) {
            LOGGER.error(ExceptionUtil.stacktraceToString(ex));
        }finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
