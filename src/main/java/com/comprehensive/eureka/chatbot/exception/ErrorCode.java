package com.comprehensive.eureka.chatbot.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(20000, "INTERNAL_SERVER_ERROR",  "서버 내부 오류가 발생했습니다."),

    // 금칙어 관리 에러 (20010~20019)
    FORBIDDEN_WORD_NOT_FOUND       (20010, "FORBIDDEN_WORD_NOT_FOUND",       "해당 금칙어를 찾을 수 없습니다."),
    FORBIDDEN_WORD_ALREADY_EXISTS  (20011, "FORBIDDEN_WORD_ALREADY_EXISTS",  "이미 등록된 금칙어입니다."),
    FORBIDDEN_WORD_CREATE_FAILED   (20012, "FORBIDDEN_WORD_CREATE_FAILED",   "금칙어 등록에 실패했습니다."),
    FORBIDDEN_WORD_UPDATE_FAILED   (20013, "FORBIDDEN_WORD_UPDATE_FAILED",   "금칙어 수정에 실패했습니다."),
    FORBIDDEN_WORD_DELETE_FAILED   (20014, "FORBIDDEN_WORD_DELETE_FAILED",   "금칙어 삭제에 실패했습니다."),
    FORBIDDEN_WORD_TOGGLE_FAILED   (20015, "FORBIDDEN_WORD_TOGGLE_FAILED",   "금칙어 사용 여부 전환에 실패했습니다."),

    // 금칙어 로그 관리 에러 (20020~20029)
    FORBIDDEN_WORD_LOG_NOT_FOUND       (20020, "FORBIDDEN_WORD_LOG_NOT_FOUND",       "해당 금칙어 로그를 찾을 수 없습니다."),
    FORBIDDEN_WORD_LOG_RETRIEVE_FAILED (20021, "FORBIDDEN_WORD_LOG_RETRIEVE_FAILED", "금칙어 로그 조회에 실패했습니다."),
    FORBIDDEN_WORD_LOG_DELETE_FAILED   (20022, "FORBIDDEN_WORD_LOG_DELETE_FAILED",   "금칙어 로그 삭제에 실패했습니다.");

    private final int code;
    private final String name;
    private final String message;
}