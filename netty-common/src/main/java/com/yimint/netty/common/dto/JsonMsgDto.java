package com.yimint.netty.common.dto;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;

@Data
public class JsonMsgDto {

    private int id;
    private String content;

    public JsonMsgDto() {
        this.id = RandomUtil.randomInt(100);
    }

    public static JsonMsgDto parse(String jsonStr) {
        return JSONUtil.toBean(jsonStr, JsonMsgDto.class);
    }

    public static String format(JsonMsgDto jsonMsgDto) {
        return JSONUtil.toJsonStr(jsonMsgDto);
    }
}
