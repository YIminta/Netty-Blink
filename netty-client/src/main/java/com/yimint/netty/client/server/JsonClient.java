package com.yimint.netty.client.server;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.yimint.netty.common.coder.JsonMsgEncoder;
import com.yimint.netty.common.dto.JsonMsgDto;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @Auther：yimint
 * @Date: 2023-08-02 10:46
 * @Description
 */
public class JsonClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(JsonClient.class);
    public static final String SERVER_IP = "127.0.0.1";
    public static final int SERVER_PORT = 54123;

    public static void main(String[] args) {
        Bootstrap b = new Bootstrap();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            b.group(workGroup);
            b.channel(NioSocketChannel.class);
            b.remoteAddress(SERVER_IP, SERVER_PORT);
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,10000);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new LengthFieldPrepender(4));
                    socketChannel.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
                    socketChannel.pipeline().addLast(new JsonMsgEncoder());
                }
            });
            ChannelFuture channelFuture = b.connect();
            channelFuture.addListener((ChannelFuture futureListener) -> {
                if (futureListener.isSuccess()) {
                    LOGGER.info("EchoClient客户端连接成功!");
                } else {
                    LOGGER.info("EchoClient客户端连接失败!");
                }
            });
            channelFuture.sync();
            Channel channel = channelFuture.channel();
            Scanner scanner = new Scanner(System.in);
            LOGGER.info("请输入发送内容:");
            // 发送回调监听
            GenericFutureListener sendCallBack = future -> {
                if (future.isSuccess()) {
                    LOGGER.info("发送成功!");
                } else {
                    LOGGER.info("发送失败!");
                }
            };

            while (scanner.hasNext()) {
                //获取输入的内容
                String next = scanner.next();
                JsonMsgDto jsonMsgDto = new JsonMsgDto();
                jsonMsgDto.setContent(next);
                ChannelFuture writeAndFlushFuture = channel.writeAndFlush(jsonMsgDto);
                writeAndFlushFuture.addListener(sendCallBack);
                LOGGER.info("请输入发送内容:");
            }
        } catch (Exception ex) {
            LOGGER.error(ExceptionUtil.stacktraceToString(ex));
        }finally {
            workGroup.shutdownGracefully();
        }
    }
}
