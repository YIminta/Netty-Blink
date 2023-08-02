package com.yimint.netty.common.dto;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * @Auther：yimint
 * @Date: 2023-08-02 10:16
 * @Description
 */
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

    public static String format(Object obj) {
        return JSONUtil.toJsonStr(obj);
    }
}
