package com.comprehensive.eureka.chatbot.common.exception;


public class DomainException extends RuntimeException{
    private final ErrorCode ec;

    public DomainException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.ec = errorCode;
    }
}
