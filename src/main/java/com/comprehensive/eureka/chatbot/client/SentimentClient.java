package com.comprehensive.eureka.chatbot.client;

import com.comprehensive.eureka.chatbot.badword.dto.request.UserForbiddenWordsChatCreateRequestDto;
import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import com.comprehensive.eureka.chatbot.langchain.dto.DetermineSentimentDto;
import com.comprehensive.eureka.chatbot.langchain.dto.DetermineSentimentResponseDto;
import com.comprehensive.eureka.chatbot.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SentimentClient {
    private final WebClientUtil webClientUtil;

    public BaseResponseDto<DetermineSentimentResponseDto> determineSentiment(DetermineSentimentDto determineSentimentDto) {
        BaseResponseDto<DetermineSentimentResponseDto> post = webClientUtil.post(
                "http://localhost:8088/sentiment/api/predict",
                determineSentimentDto,
                new ParameterizedTypeReference<>() {
                }
        );
        log.info("post값"+post);
        return post;
    }

}