package com.yimint.netty.client.server;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.yimint.netty.client.handler.EchoClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class EchoClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(EchoClient.class);
    public static final String SERVER_IP = "127.0.0.1";
    public static final int SERVER_PORT = 54123;

    public static void main(String[] args) {
        //Step1: 创建组装器 - 用于配置客户端的 - 事件轮询器 - 通道 - 处理器
        Bootstrap b = new Bootstrap();
        //Step2: 创建轮询器 - 封装了Selector, 用于选择数据传输事件
        EventLoopGroup workerLoopGroup = new NioEventLoopGroup();

        try {
            b.group(workerLoopGroup);
            //Step3.1:设置通道类型
            b.channel(NioSocketChannel.class);
            //Step3.2:设置监听端口
            b.remoteAddress(SERVER_IP, SERVER_PORT);
            //Step3.3:设置通道的参数
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            //默认是30s, 如果在给定的时间不能成功建立连接或者被丢弃掉，将抛出ConnectTimeoutException
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,10000);
            b.option(ChannelOption.SO_KEEPALIVE, true);;
            //Step4: 配置事件处理器
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(EchoClientHandler.INSTANCE);
                }
            });
            //Step5: 循环链接服务端
            ChannelFuture f = null;
            boolean connected = false;
            while (!connected) {
                f = b.connect();
                f.addListener((ChannelFuture futureListener) -> {
                    if (futureListener.isSuccess()) {
                        LOGGER.info("EchoClient客户端连接成功!");
                    } else {
                        LOGGER.info("EchoClient客户端连接失败!");
                    }
                });
                // sync作用: 因为上面的连接到服务器上以及监听都是异步操作, 执行后马上返回, 可能连接还未完全建立, 所以sync在此等待一下
                // f.sync(); 发生错误会抛异常
                f.awaitUninterruptibly();//发生错误不会抛异常
                if (f.isCancelled()) {
                    LOGGER.info("用户取消连接:");
                    return;
                    // Connection attempt cancelled by user
                } else if (f.isSuccess()) {
                    connected = true;
                }
            }

            //StepX - 业务操作: 在连接完成之后, 获取到通道, 往通道里面写一些数据
            //获取通道
            Channel channel = f.channel();
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
                byte[] bytes = (DateUtil.now() + " >>" + next).getBytes(StandardCharsets.UTF_8);
                // 创建一个缓冲区, 用于存储待发送的信息
                ByteBuf buffer = channel.alloc().buffer();
                // 保存数据到直接内存的缓冲区
                buffer.writeBytes(bytes);
                // 通过通道将数据发送出去
                ChannelFuture writeAndFlushFuture = channel.writeAndFlush(buffer);
                writeAndFlushFuture.addListener(sendCallBack);
                LOGGER.info("请输入发送内容:");

            }
        } catch (Exception ex) {
            ExceptionUtil.stacktraceToString(ex);
        } finally {
            workerLoopGroup.shutdownGracefully();
        }
    }
}
