package com.yimint.netty.client.server;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.RandomUtil;
import com.yimint.netty.common.coder.JsonMsgEncoder;
import com.yimint.netty.common.dto.JsonMsgDto;
import com.yimint.netty.common.protocol.MessageProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * @Auther：teddy
 * @Date: 2023/8/2 22:29
 * @Description
 */
public class ProtoBufClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProtoBufClient.class);
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
                    // 客户端channel流水线添加2个handler处理器
                    /**
                     * ProtobufVarint32LengthFieldPrepender 是 Netty 提供的编码器。它的作用是在消息的头部添加一个表示消息长度的字段，以便接收方可以正确解析出消息的长度。
                     * 通常搭配 ProtobufVarint32FrameDecoder 一起使用，发送方会在消息前添加一个 Varint32 类型的字段，表示消息的长度，接收方会根据该长度字段来拆分消息，保证接收到完整的消息数据
                     */
                    socketChannel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                    socketChannel.pipeline().addLast(new ProtobufEncoder());
                }
            });
            ChannelFuture channelFuture = b.connect();
            channelFuture.addListener((ChannelFuture futureListener) -> {
                if (futureListener.isSuccess()) {
                    LOGGER.info("ProtoBufClient客户端连接成功!");
                } else {
                    LOGGER.info("ProtoBufClient客户端连接失败!");
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
                MessageProto.Message message = MessageProto.Message.newBuilder().setId(RandomUtil.randomNumbers(2)).setContent(next).build();
                ChannelFuture writeAndFlushFuture = channel.writeAndFlush(message);
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
