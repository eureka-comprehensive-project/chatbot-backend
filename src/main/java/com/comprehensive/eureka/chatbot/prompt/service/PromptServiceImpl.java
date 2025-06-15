package com.comprehensive.eureka.chatbot.prompt.service;

import com.comprehensive.eureka.chatbot.common.exception.ErrorCode;
import com.comprehensive.eureka.chatbot.common.exception.PromptException;
import com.comprehensive.eureka.chatbot.prompt.dto.PromptDto;
import com.comprehensive.eureka.chatbot.prompt.entity.Prompt;
import com.comprehensive.eureka.chatbot.prompt.repository.PromptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PromptServiceImpl implements PromptService {

    private final PromptRepository promptRepository;

    @Override
    public PromptDto createPrompt(PromptDto promptDto) {
        int sentimentCode = promptDto.getSentimentCode();
        String name = promptDto.getName();

        if (promptRepository.existsBySentimentCode(sentimentCode)) {
            throw new PromptException(ErrorCode.SENTIMENT_CODE_ALREADY_EXISTS);
        }

        if (promptRepository.existsByName(name)) {
            throw new PromptException(ErrorCode.SENTIMENT_NAME_ALREADY_EXISTS);
        }

        try {
            Prompt prompt = Prompt.builder()
                    .sentimentCode(promptDto.getSentimentCode())
                    .name(promptDto.getName())
                    .scenario(promptDto.getScenario())
                    .build();
            promptRepository.save(prompt);

        } catch (Exception e) {
            throw new PromptException(ErrorCode.PROMPT_CREATE_FAILED);
        }

        return promptDto;
    }

    @Override
    public List<PromptDto> getAllPrompts() {
        List<PromptDto> prompts = new ArrayList<>();

        try {
            promptRepository.findAll().forEach(prompt -> {
                PromptDto promptDto = PromptDto.builder()
                        .promptId(prompt.getPromptId())
                        .sentimentCode(prompt.getSentimentCode())
                        .name(prompt.getName())
                        .scenario(prompt.getScenario())
                        .build();
                prompts.add(promptDto);
            });

        } catch (Exception e) {
            throw new PromptException(ErrorCode.PROMPT_NOT_FOUND);
        }

        return prompts;
    }

    @Override
    public PromptDto getPromptBySentimentName(String sentimentName) {
        System.out.println(promptRepository.existsByName(sentimentName));
        Prompt prompt = promptRepository.findByName(sentimentName)
                .orElseThrow(() -> new PromptException(ErrorCode.PROMPT_NOT_FOUND));

        return PromptDto.builder()
                .promptId(prompt.getPromptId())
                .sentimentCode(prompt.getSentimentCode())
                .name(prompt.getName())
                .scenario(prompt.getScenario())
                .build();
    }

    @Override
    public PromptDto updatePrompt(PromptDto promptDto) {

        Prompt prompt = Prompt.builder()
                .promptId(promptDto.getPromptId())
                .sentimentCode(promptDto.getSentimentCode())
                .name(promptDto.getName())
                .scenario(promptDto.getScenario())
                .build();

        try {
            promptRepository.save(prompt);

        } catch (Exception e) {
            throw new PromptException(ErrorCode.PROMPT_UPDATE_FAILED);
        }

        return promptDto;
    }

    @Override
    public void deletePrompt(Long promptId) {
        try {
            promptRepository.deleteById(promptId);

        } catch (Exception e) {
            throw new PromptException(ErrorCode.PROMPT_DELETE_FAILED);
        }
    }
}
