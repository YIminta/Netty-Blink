package com.yimint.blink.exception;

/**
 * @Auther：yimint
 * @Date: 2023-08-15 09:40
 * @Description
 */
public class BlinkException extends RuntimeException {
    public BlinkException(String message) {
        super(message);
    }

    public BlinkException(String message, Throwable cause) {
        super(message, cause);
    }

    public BlinkException(StatusEnum statusEnum) {
        super(statusEnum.getMessage());
    }
}
