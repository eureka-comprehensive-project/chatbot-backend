package com.comprehensive.eureka.chatbot.client;

import com.comprehensive.eureka.chatbot.badword.dto.request.UserForbiddenWordsChatCreateRequestDto;
import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import com.comprehensive.eureka.chatbot.constant.DomainConstant;
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
        String url = DomainConstant.SENTIMENT_DOMAIN;
        String sentiment = webClientUtil.postSentiment(
                url+"/sentiment/api/predict",
                determineSentimentDto
        );
        log.info("\""+ determineSentimentDto.getMessage() +"\"의 감정 예상 : " + sentiment);
        return sentiment;
    }

}