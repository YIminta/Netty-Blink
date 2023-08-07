package com.yimint.netty.server.sevice;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.yimint.netty.common.protocol.MessageProto;
import com.yimint.netty.server.handler.ProtoBufHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther：teddy
 * @Date: 2023/8/2 22:06
 * @Description
 */
public class ProtoBufServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProtoBufServer.class);
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
                    // pipeline管理子通道channel中的Handler
                    // 向子channel流水线添加3个handler处理器

                    // protobufDecoder仅仅负责编码，并不支持读半包，所以在之前，一定要有读半包的处理器。
                    // 有三种方式可以选择：
                    // 使用netty提供ProtobufVarint32FrameDecoder
                    // 继承netty提供的通用半包处理器 LengthFieldBasedFrameDecoder
                    // 继承ByteToMessageDecoder类，自己处理半包

                    // 半包的处理
                    socketChannel.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                    // 需要解码的目标类
                    socketChannel.pipeline().addLast(new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
                    socketChannel.pipeline().addLast(new ProtoBufHandler());
                }
            });
            ChannelFuture channelFuture = b.bind();
            channelFuture.addListener((future) -> {
                if (future.isSuccess()) {
                    LOGGER.info(" ========》反应器线程 回调 Protobuf服务器启动成功，监听端口: " +
                            channelFuture.channel().localAddress());

                }
            });
            channelFuture.sync();
            LOGGER.info(" 调用线程执行的，Protobuf服务器启动成功，监听端口: " +
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
