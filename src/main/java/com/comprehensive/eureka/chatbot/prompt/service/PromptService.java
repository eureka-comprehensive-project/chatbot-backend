package com.comprehensive.eureka.chatbot.prompt.service;

import com.comprehensive.eureka.chatbot.prompt.dto.PromptDto;

import java.util.List;

public interface PromptService {

    PromptDto createPrompt(PromptDto promptDto);

    List<PromptDto> getAllPrompts();
    PromptDto getPromptBySentimentCode(Integer sentimentCode);

    PromptDto updatePrompt(PromptDto promptDto);
    void deletePrompt(Long promptId);
}
