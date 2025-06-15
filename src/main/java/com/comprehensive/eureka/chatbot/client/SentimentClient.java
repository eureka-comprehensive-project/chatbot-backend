package com.comprehensive.eureka.chatbot.client;

import com.comprehensive.eureka.chatbot.badword.dto.request.UserForbiddenWordsChatCreateRequestDto;
import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import com.comprehensive.eureka.chatbot.langchain.dto.DetermineSentimentDto;
import com.comprehensive.eureka.chatbot.langchain.dto.DetermineSentimentResponseDto;
import com.comprehensive.eureka.chatbot.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SentimentClient {
    private final WebClientUtil webClientUtil;

    public BaseResponseDto<DetermineSentimentResponseDto> determineSentiment(DetermineSentimentDto determineSentimentDto) {
        return webClientUtil.post(
                "http://localhost:8088/sentiment/api/predict",
                determineSentimentDto,
                new ParameterizedTypeReference<>() {
                }
        );
    }

}