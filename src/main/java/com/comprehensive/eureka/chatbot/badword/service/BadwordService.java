package com.comprehensive.eureka.chatbot.badword.service;

import com.comprehensive.eureka.chatbot.badword.dto.request.BadwordRequest;
import com.comprehensive.eureka.chatbot.badword.dto.response.BadwordResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;


public interface BadwordService {
    BadwordResponse createBadWord(BadwordRequest BadWordResponse);
    List<BadwordResponse> getAllBadWord();
    void deleteBadWordResponse(String word);

    boolean checkBadWord(String message) throws JsonProcessingException;
    void sendBadwordRecord(Long userId, Long chatMesageId, String message);
}
