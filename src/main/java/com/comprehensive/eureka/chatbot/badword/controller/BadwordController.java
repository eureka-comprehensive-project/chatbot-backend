package com.comprehensive.eureka.chatbot.badword.controller;

import com.comprehensive.eureka.chatbot.badword.dto.BaseResponseDto;
import com.comprehensive.eureka.chatbot.badword.dto.request.BadwordRequest;
import com.comprehensive.eureka.chatbot.badword.dto.response.BadwordResponse;
import com.comprehensive.eureka.chatbot.badword.service.BadwordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badwords")
public class BadwordController {

    private final BadwordService badwordService;

    @Autowired
    public BadwordController(BadwordService badwordService) {
        this.badwordService = badwordService;
    }

    // CREATE
    @PostMapping
    public BaseResponseDto<BadwordResponse> createBadword(@RequestBody BadwordRequest badword) {
        return BaseResponseDto.success(badwordService.createBadWord(badword));
    }

    // DELETE
    @DeleteMapping("/{word}")
    public void deleteBadword(@PathVariable String word) {
        badwordService.deleteBadWordResponse(word);
    }
}
