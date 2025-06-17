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

    public String determineSentiment(DetermineSentimentDto determineSentimentDto) {
        String sentiment = webClientUtil.postSentiment(
                "http://localhost:8088/sentiment/api/predict",
//                "https://www.visiblego.com/sentiment/api/predict",
                determineSentimentDto
        );
        log.info("post값"+sentiment);
        return sentiment;
    }

}