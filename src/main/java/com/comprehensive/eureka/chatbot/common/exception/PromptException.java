package com.comprehensive.eureka.chatbot.common.exception;

import lombok.Getter;

@Getter
public class PromptException extends RuntimeException {

    private final ErrorCode errorCode;

    public PromptException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
