package com.comprehensive.eureka.chatbot.badword.service;

import com.comprehensive.eureka.chatbot.badword.dto.request.BadwordRequest;
import com.comprehensive.eureka.chatbot.badword.dto.request.UserForbiddenWordsChatCreateRequestDto;
import com.comprehensive.eureka.chatbot.badword.dto.response.BadwordResponse;
import com.comprehensive.eureka.chatbot.badword.dto.response.ForbiddenWordResponseDto;
import com.comprehensive.eureka.chatbot.badword.redis.service.ForbiddenWordRedisService;
import com.comprehensive.eureka.chatbot.badword.redis.service.RedisService;
import com.comprehensive.eureka.chatbot.client.AdminClient;
import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BadwordServiceImpl implements BadwordService {

    private final ForbiddenWordRedisService redisService;
    private final AdminClient adminClient;


    @Override
    public void createBadWord(BadwordRequest badwordRequest) {
        List<ForbiddenWordResponseDto> list = null;
        try{
            redisService.addForbiddenWord(badwordRequest.getBadword());
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("금칙어 리스트에 단어 추가하는 중에 오류가 발생");
        }
    }

    @Override
    public Set<String> getAllForbiddendWords() {
        return redisService.getAllForbiddenWords();
    }


    @Override
    public boolean checkBadWord(String message){
        Set<String> badWords = getAllForbiddendWords();
        for (String word : badWords) {
            if (message.contains(word)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void deleteBadWord(String word) {
        redisService.removeForbiddenWord(word);
    }

    @Override
    public void sendBadwordRecord(Long userId,Long chatMessageId, String message){
        Set<String> badWords = getAllForbiddendWords();
        List<String> found = new ArrayList<>();
        for (String word : badWords) {
            if (message.contains(word)) {
                found.add(word);
            }
        }
        log.info("found" + found + "userId" + userId + "chatId" + chatMessageId );

        UserForbiddenWordsChatCreateRequestDto userForbiddenWordsChatCreateRequestDto = UserForbiddenWordsChatCreateRequestDto.builder()
                .userId(userId)
                .chatMessageId(chatMessageId)
                .forbiddenWords(found)
                .build();

        adminClient.insertForbiddenWordRecord(userForbiddenWordsChatCreateRequestDto);
    }
}
