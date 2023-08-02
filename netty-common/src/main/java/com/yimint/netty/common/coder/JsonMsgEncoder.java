package com.yimint.netty.common.coder;

import com.yimint.netty.common.dto.JsonMsgDto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @Auther：yimint
 * @Date: 2023-08-02 10:16
 * @Description
 */
public class JsonMsgEncoder extends MessageToMessageEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object obj, List<Object> list) throws Exception {
        String json = JsonMsgDto.format(obj);
        System.out.println("发送报文：" + json);
        list.add(json);
    }
}
