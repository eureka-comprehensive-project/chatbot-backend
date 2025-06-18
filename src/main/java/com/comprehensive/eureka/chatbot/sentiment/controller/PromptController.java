package com.comprehensive.eureka.chatbot.sentiment.controller;

import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import com.comprehensive.eureka.chatbot.sentiment.dto.PromptDto;
import com.comprehensive.eureka.chatbot.sentiment.service.PromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chatbot/api/prompt")
@RequiredArgsConstructor
public class PromptController {

    private final PromptService promptService;

    @PostMapping
    public ResponseEntity<BaseResponseDto> createPrompt(@RequestBody PromptDto promptDto) {
        PromptDto saved = promptService.createPrompt(promptDto);

        return ResponseEntity.ok(BaseResponseDto.success(saved));
    }

    @GetMapping
    public ResponseEntity<BaseResponseDto> getAllPrompts() {
        List<PromptDto> prompts = promptService.getAllPrompts();

        return ResponseEntity.ok(BaseResponseDto.success(prompts));
    }


    @PutMapping
    public ResponseEntity<BaseResponseDto> updatePrompt(@RequestBody PromptDto promptDto) {
        PromptDto updated = promptService.updatePrompt(promptDto);

        return ResponseEntity.ok(BaseResponseDto.success(updated));
    }

    @DeleteMapping("/{promptId}")
    public ResponseEntity<BaseResponseDto> deletePrompt(@PathVariable Long promptId) {
        promptService.deletePrompt(promptId);

        return ResponseEntity.ok(BaseResponseDto.voidSuccess());
    }
}
