package com.comprehensive.eureka.chatbot.sentiment.service;

import com.comprehensive.eureka.chatbot.sentiment.dto.PromptDto;

import java.util.List;

public interface PromptService {

    PromptDto createPrompt(PromptDto promptDto);

    List<PromptDto> getAllPrompts();


    PromptDto getPromptBySentimentName(String sentimentName);

    PromptDto updatePrompt(PromptDto promptDto);
    void deletePrompt(Long promptId);
}
