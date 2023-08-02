package com.yimint.netty.common.coder;

import com.yimint.netty.common.dto.JsonMsgDto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @Auther：yimint
 * @Date: 2023-08-02 10:16
 * @Description
 */
public class JsonMsgDecoder extends MessageToMessageDecoder<String> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, String str, List<Object> list) throws Exception {
        JsonMsgDto json = JsonMsgDto.parse(str);
        System.out.println("收到报文："+JsonMsgDto.format(str));
        list.add(json);
    }
}
