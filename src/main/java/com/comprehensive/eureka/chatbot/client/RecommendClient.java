package com.comprehensive.eureka.chatbot.client;

import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import com.comprehensive.eureka.chatbot.langchain.dto.PlanRecommendationDto;
import com.comprehensive.eureka.chatbot.langchain.dto.TelecomProfile;
import com.comprehensive.eureka.chatbot.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecommendClient {
    private final WebClientUtil webClientUtil;

    public BaseResponseDto<PlanRecommendationDto> recommend(TelecomProfile telecomProfile) {
        return webClientUtil.post(
                "http://localhost:8083/recommend/1",
                telecomProfile,
                new ParameterizedTypeReference<>() {
                }
        );
    }
}
