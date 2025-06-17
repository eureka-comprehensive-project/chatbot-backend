package com.comprehensive.eureka.chatbot.client;

import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import com.comprehensive.eureka.chatbot.langchain.dto.BenefitRequestDto;
import com.comprehensive.eureka.chatbot.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlanClient {

    private final WebClientUtil webClientUtil;
    public Long getBenefitIds(BenefitRequestDto requestDto) {

//        String apiUrl = "http://localhost:8084/plan/benefit/benefit-group";
        String apiUrl = "https://www.visiblego.com/plan/benefit/benefit-group";

        BaseResponseDto<Long> response = webClientUtil.post(
                apiUrl,
                requestDto,
                new ParameterizedTypeReference<>() {}
        );
        return response.getData();
    }
}