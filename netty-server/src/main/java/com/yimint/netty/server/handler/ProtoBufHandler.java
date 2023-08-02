package com.yimint.netty.server.handler;

import com.yimint.netty.common.protocol.MessageProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther：teddy
 * @Date: 2023/8/2 22:14
 * @Description
 */
public class ProtoBufHandler extends ChannelInboundHandlerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProtoBufHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageProto.Message protoMsg = (MessageProto.Message) msg;
        //经过pipeline的各个decoder，到此Person类型已经可以断定
        LOGGER.info("收到一个 MsgProtos.Msg 数据包 =》");
        LOGGER.info("protoMsg.getId():=" + protoMsg.getId());
        LOGGER.info("protoMsg.getContent():=" + protoMsg.getContent());
    }
}
