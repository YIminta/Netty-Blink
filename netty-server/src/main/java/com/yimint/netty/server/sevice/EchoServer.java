package com.yimint.netty.server.sevice;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.yimint.netty.server.handler.EchoServiceHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(EchoServer.class);
    public static final String SERVER_IP = "127.0.0.1";
    public static final int SERVER_PORT = 54123;

    public static void main(String[] args) {
        ServerBootstrap b = new ServerBootstrap();
        //创建reactor 线程组
        EventLoopGroup bossLoopGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerLoopGroup = new NioEventLoopGroup();

        try {
            //1 设置reactor 线程组
            b.group(bossLoopGroup, workerLoopGroup);
            //2 设置nio类型的channel
            b.channel(NioServerSocketChannel.class);
            //3 设置监听端口
            b.localAddress(SERVER_PORT);
            //4 设置通道的参数
            /**
             *PooledByteBufAllocator 是 Netty 中默认的字节缓冲分配器实现。它使用池化的方式来管理字节缓冲，重用之前分配过的缓冲，从而减少内存分配和回收的开销。通过使用池化的方式，它能够在高并发情况下更高效地管理缓冲，适用于大量的、频繁的缓冲分配和回收操作。
             *PooledByteBufAllocator 适合处理长期运行的网络应用程序，特别是在高并发的情况下，它可以显著提高性能和内存利用率。
             */
            //b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            /**
             * UnpooledByteBufAllocator 是 Netty 中另一种字节缓冲分配器实现。它不使用池化机制，每次请求都会创建一个新的字节缓冲对象，每个缓冲对象在使用完后都会被单独回收。因此，它的开销较大，不具备池化的优势。
             * UnpooledByteBufAllocator 适合处理短期、简单的网络应用程序，特别是在并发较低的情况下，或者在需要临时创建大量缓冲时。
             */
            b.option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
            b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            b.childOption(ChannelOption.SO_KEEPALIVE, true);

            //5 装配子通道流水线
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(EchoServiceHandler.INSTANCE);
                }
            });

            // 6 开始绑定server
            // 通过调用sync同步方法阻塞直到绑定成功
            ChannelFuture channelFuture = b.bind();
            channelFuture.addListener((future)->{
                if(future.isSuccess())
                {
                    LOGGER.info(" ========》反应器线程 回调 服务器启动成功，监听端口: " +
                            channelFuture.channel().localAddress());

                }
            });
//            channelFuture.sync();
            LOGGER.info(" 调用线程执行的，服务器启动成功，监听端口: " +
                    channelFuture.channel().localAddress());

            // 7 等待通道关闭的异步任务结束
            // 服务监听通道会一直等待通道关闭的异步任务结束
            ChannelFuture closeFuture = channelFuture.channel().closeFuture();
            closeFuture.sync();
        } catch (Exception ex) {
            ExceptionUtil.stacktraceToString(ex);
        } finally {
            // 8 优雅关闭EventLoopGroup，
            // 释放掉所有资源包括创建的线程
            workerLoopGroup.shutdownGracefully();
            bossLoopGroup.shutdownGracefully();
        }
    }
}
