package com.comprehensive.eureka.chatbot.client;

import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import com.comprehensive.eureka.chatbot.langchain.dto.RecommendPlanDto;
import com.comprehensive.eureka.chatbot.langchain.dto.UserPreferenceDto;
import com.comprehensive.eureka.chatbot.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendClient {
    private final WebClientUtil webClientUtil;

    // 통신 성향 기반 추천
    public BaseResponseDto<List<RecommendPlanDto>> recommend(UserPreferenceDto preference) {
        String url = "https://www.visiblego.com/recommend";
        log.info("추천 모듈로 환경설정 전송 중: {}", preference);
        return webClientUtil.post(url, preference, new ParameterizedTypeReference<BaseResponseDto<List<RecommendPlanDto>>>() {});
    }

    // 키워드 기반 추천
    public BaseResponseDto<List<RecommendPlanDto>> recommendByKeyword(String keyword) {
        String url = String.format("https://www.visiblego.com/recommend/keyword/%s", keyword);
        log.info("키워드를 추천 모듈로 전송 중: {}", keyword);

        return webClientUtil.<List<RecommendPlanDto>>getWithPathVariable(
                url,
                keyword,
                new ParameterizedTypeReference<BaseResponseDto<List<RecommendPlanDto>>>() {
                }
        );
    }
}