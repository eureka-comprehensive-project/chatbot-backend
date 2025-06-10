package com.comprehensive.eureka.chatbot.badword.service;

import com.comprehensive.eureka.chatbot.badword.dto.request.BadwordRequest;
import com.comprehensive.eureka.chatbot.badword.dto.response.BadwordResponse;

import java.util.List;


public interface BadwordService {
    BadwordResponse createBadWord(BadwordRequest BadWordResponse);
    List<BadwordResponse> getAllBadWord();
    void deleteBadWordResponse(String word);
}
