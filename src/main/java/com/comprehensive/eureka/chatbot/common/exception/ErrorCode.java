package com.comprehensive.eureka.chatbot.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(20000, "INTERNAL_SERVER_ERROR",  "서버 내부 오류가 발생했습니다."),

    // 프롬프트 관리 에러 (20010~20019)
    PROMPT_NOT_FOUND       (20010, "PROMPT_NOT_FOUND", "해당 프롬프트를 찾을 수 없습니다."),
    SENTIMENT_CODE_ALREADY_EXISTS  (20011, "SENTIMENT_CODE_ALREADY_EXISTS", "이미 등록된 감정코드입니다."),
    SENTIMENT_NAME_ALREADY_EXISTS  (20011, "SENTIMENT_NAME_ALREADY_EXISTS", "이미 등록된 감정입니다."),
    PROMPT_CREATE_FAILED   (20012, "PROMPT_CREATE_FAILED", "프롬프트 등록에 실패했습니다."),
    PROMPT_UPDATE_FAILED   (20013, "PROMPT_UPDATE_FAILED", "프롬프트 수정에 실패했습니다."),
    PROMPT_DELETE_FAILED   (20014, "PROMPT_DELETE_FAILED", "프롬프트 삭제에 실패했습니다.");

    // // 프롬프트 로그 관리 에러 (20020~20029)
    // FORBIDDEN_WORD_LOG_NOT_FOUND       (20020, "FORBIDDEN_WORD_LOG_NOT_FOUND",       "해당 금칙어 로그를 찾을 수 없습니다."),
    // FORBIDDEN_WORD_LOG_RETRIEVE_FAILED (20021, "FORBIDDEN_WORD_LOG_RETRIEVE_FAILED", "금칙어 로그 조회에 실패했습니다."),
    // FORBIDDEN_WORD_LOG_DELETE_FAILED   (20022, "FORBIDDEN_WORD_LOG_DELETE_FAILED",   "금칙어 로그 삭제에 실패했습니다."),
    //
    // // 사용자_금칙어_채팅 기록 조회 에러 (20030~20039)
    // USER_FORBIDDEN_WORDS_CHAT_NOT_FOUND      (20030, "USER_FORBIDDEN_WORDS_CHAT_NOT_FOUND",      "해당 사용자의 금칙어 채팅 기록을 찾을 수 없습니다."),
    // USER_FORBIDDEN_WORDS_CHAT_RETRIEVE_FAILED(20031, "USER_FORBIDDEN_WORDS_CHAT_RETRIEVE_FAILED","사용자 금칙어 채팅 기록 조회에 실패했습니다.");

    private final int code;
    private final String name;
    private final String message;
}
