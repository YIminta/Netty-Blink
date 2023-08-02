package com.yimint.netty.server.sevice;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.yimint.netty.common.coder.JsonMsgDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther：yimint
 * @Date: 2023-08-02 10:16
 * @Description
 */
public class JsonService {
    private final static Logger LOGGER = LoggerFactory.getLogger(JsonService.class);
    public static final String SERVER_IP = "127.0.0.1";
    public static final int SERVER_PORT = 54123;

    public static void main(String[] args) {
        ServerBootstrap b = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            b.group(bossGroup, workGroup);
            b.channel(NioServerSocketChannel.class);
            b.localAddress(SERVER_PORT);
            b.option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
            b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    /**
                     * maxFrameLength: 解码的帧的最大长度，超过此长度的帧将被丢弃。
                     * lengthFieldOffset: 长度字段在帧中的偏移量，表示长度字段的起始位置。
                     * lengthFieldLength: 长度字段的长度，通常为 2、4、8 等字节。
                     * lengthAdjustment: 长度字段的值与帧的实际长度之间的偏差值。
                     * initialBytesToStrip: 解码后，从帧中跳过的字节数。
                     */
                    socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4));
                    socketChannel.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
                    socketChannel.pipeline().addLast(new JsonMsgDecoder());
                }
            });
            ChannelFuture channelFuture = b.bind();
            channelFuture.addListener((future) -> {
                if (future.isSuccess()) {
                    LOGGER.info(" ========》反应器线程 回调 Json服务器启动成功，监听端口: " +
                            channelFuture.channel().localAddress());

                }
            });
            channelFuture.sync();
            LOGGER.info(" 调用线程执行的，Json服务器启动成功，监听端口: " +
                    channelFuture.channel().localAddress());

            ChannelFuture closeFuture = channelFuture.channel().closeFuture();
            closeFuture.sync();
        } catch (Exception ex) {
            ExceptionUtil.stacktraceToString(ex);
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}
