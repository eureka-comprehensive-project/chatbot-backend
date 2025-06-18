package com.comprehensive.eureka.chatbot.langchain.dto;

public class FeedbackResponseDto {
    private Long feedbackCode;

    // 기본 생성자
    public FeedbackResponseDto() {}

    // getter / setter
    public Long getFeedbackCode() {
        return feedbackCode;
    }

    public void setFeedbackCode(Long feedbackCode) {
        this.feedbackCode = feedbackCode;
    }

    @Override
    public String toString() {
        return "FeedbackResponseDto{feedbackCode=" + feedbackCode + "}";
    }
}
