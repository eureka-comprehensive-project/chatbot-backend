package com.comprehensive.eureka.chatbot.langchain.dto;

import lombok.Data;

@Data
public class PlanRecommendationDto {

    private String planName;
    private String price;
    private String description;
}
