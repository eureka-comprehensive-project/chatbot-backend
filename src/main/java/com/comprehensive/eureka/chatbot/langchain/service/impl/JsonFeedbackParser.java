package com.comprehensive.eureka.chatbot.langchain.service.impl;

import com.comprehensive.eureka.chatbot.langchain.dto.FeedbackResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonFeedbackParser {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static FeedbackResponseDto parseFeedbackResponse(String input) {
        try {
            // JSON 포맷인지 검사하고 파싱
            if (input != null && input.trim().startsWith("{") && input.contains("\"feedbackCode\"")) {
                return objectMapper.readValue(input, FeedbackResponseDto.class);
            } else {
                System.out.println("입력값에 'feedbackCode'가 포함된 JSON이 아닙니다.");
                return null;
            }
        } catch (Exception e) {
            System.err.println("JSON 파싱 오류: " + e.getMessage());
            return null;
        }
    }
}
