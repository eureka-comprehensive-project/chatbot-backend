package com.comprehensive.eureka.chatbot.badword.controller;

import com.comprehensive.eureka.chatbot.badword.dto.request.BadwordRequest;
import com.comprehensive.eureka.chatbot.badword.dto.response.BadwordResponse;
import com.comprehensive.eureka.chatbot.badword.service.BadwordServiceImpl;
import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatbot/api/badwords")
public class BadwordController {

    private final BadwordServiceImpl badwordServiceImpl;

    // CREATE
    @PostMapping
    public BaseResponseDto<BadwordResponse> createBadword(@RequestBody BadwordRequest badword) {
        return BaseResponseDto.success(badwordServiceImpl.createBadWord(badword));
    }

    // DELETE
    @DeleteMapping("/{word}")
    public void deleteBadword(@PathVariable String word) {
        badwordServiceImpl.deleteBadWordResponse(word);
    }

}
