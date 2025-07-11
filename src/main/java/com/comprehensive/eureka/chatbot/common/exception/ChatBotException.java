package com.comprehensive.eureka.chatbot.common.exception;

import lombok.Getter;

@Getter
public class ChatBotException extends RuntimeException {
    private final ErrorCode errorCode;

    public ChatBotException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}