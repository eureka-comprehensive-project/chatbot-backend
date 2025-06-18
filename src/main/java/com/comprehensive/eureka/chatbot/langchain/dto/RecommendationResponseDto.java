package com.comprehensive.eureka.chatbot.langchain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@Getter
@Builder
public class RecommendationResponseDto {
    private UserPreferenceDto userPreference;
    private Double avgDataUsage;
    List<RecommendPlanDto> recommendPlans;
}
