package com.yimint.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ChannelHandler.Sharable
public class EchoServiceHandler extends ChannelInboundHandlerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(EchoServiceHandler.class);
    public static final EchoServiceHandler INSTANCE = new EchoServiceHandler();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        LOGGER.info("msg type: " + (in.hasArray()?"堆内存":"直接内存"));
        //因为Netty4.1+默认是使用直接内存的buffer来存储Channel读到的数据, Java要进行处理这些数据, 先要拷贝到自己的堆中
        //所以这里先建立一个对应长度的堆内数组
        int len = in.readableBytes();
        byte[] arr = new byte[len];
        in.getBytes(0, arr);
        LOGGER.info("server received: " + new String(arr, "UTF-8"));

        //写回数据，异步任务
        LOGGER.info("写回前，msg.refCnt:" + (in.refCnt()));//测试一下当前缓冲区的引用数量

        ChannelFuture f = ctx.writeAndFlush(msg);
//        ChannelFuture f = ctx.pipeline().writeAndFlush(msg);
//        ChannelFuture f = ctx.channel().pipeline().writeAndFlush(msg);


        f.addListener((ChannelFuture futureListener) -> {
            LOGGER.info("写回后，msg.refCnt:" + in.refCnt());
        });

        //传递到下一个Handler - 当前案例只有一个用户自定义的Handler, 其实不写也无所谓.
        //super.channelRead(ctx, msg);
    }
}
