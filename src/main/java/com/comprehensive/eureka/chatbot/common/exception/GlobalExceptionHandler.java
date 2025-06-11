package com.comprehensive.eureka.chatbot.common.exception;

import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import com.comprehensive.eureka.chatbot.common.dto.ErrorResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     *  PromptException 처리
     *  ErrorCode에 담긴 코드/메시지를 BaseResponseDto.fail() 로 감싸서 반환
     */
    @ExceptionHandler(PromptException.class)
    public ResponseEntity<BaseResponseDto<ErrorResponseDto>> handlePromptException(PromptException ex) {
        ErrorCode ec = ex.getErrorCode();
        return ResponseEntity
                .badRequest()
                .body(BaseResponseDto.fail(ec));
    }

    /**
     * 그 외 예기치 못한 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponseDto<ErrorResponseDto>> handleException(Exception ex) {
        ErrorCode ec = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(500)
                .body(BaseResponseDto.fail(ec));
    }
}
