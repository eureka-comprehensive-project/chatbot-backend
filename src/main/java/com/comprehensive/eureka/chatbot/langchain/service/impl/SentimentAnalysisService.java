package com.comprehensive.eureka.chatbot.langchain.service.impl;

import com.comprehensive.eureka.chatbot.client.SentimentClient;
import com.comprehensive.eureka.chatbot.langchain.dto.DetermineSentimentDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class SentimentAnalysisService {
    private final SentimentClient sentimentClient;
    private final ObjectMapper objectMapper;
    public String analysisSentiment(String message ) throws JsonProcessingException {
        log.info("감정 분석 시작");
        String sentimentJson = sentimentClient.determineSentiment(new DetermineSentimentDto(message));
        String sentiment = objectMapper.readTree(sentimentJson).get("sentiment").asText();
        log.info("감정 분석 결과: " + sentiment);
        return sentiment;
    }
}
