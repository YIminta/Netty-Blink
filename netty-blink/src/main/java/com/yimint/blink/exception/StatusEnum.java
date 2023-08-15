package com.yimint.blink.exception;

/**
 * @Auther：yimint
 * @Date: 2023-08-15 9:53
 * @Description
 */
public enum StatusEnum {
    URL_NOT_FOUND("404", "url not match method")
    , CLASS_NOT_NULL("500", "main class is not allow be null");

    private final String code;

    private final String message;

    StatusEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
