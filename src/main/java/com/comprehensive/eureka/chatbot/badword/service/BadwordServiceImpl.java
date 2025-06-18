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
    public Set<String> getAllForbiddendWords() {

        //redis 직접 연결

        return new HashSet<>(redisService.getAllForbiddenWords());
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
    public void sendBadwordRecord(Long userId,Long chatMessageId, String message){
        Set<String> badWords = getAllForbiddendWords();
        log.info("등록된 금칙어 개수" + chatMessageId );
        List<String> found = new ArrayList<>();
        for (String word : badWords) {
            if (message.contains(word)) {
                found.add(word);
            }
        }
        log.info("금칙어 사용 기록 insert 중 -> 사용한 금칙어 개수 : " + found.size() + "userId : " + userId + "chatId : " + chatMessageId );

        UserForbiddenWordsChatCreateRequestDto userForbiddenWordsChatCreateRequestDto = UserForbiddenWordsChatCreateRequestDto.builder()
                .userId(userId)
                .chatMessageId(chatMessageId)
                .forbiddenWords(found)
                .build();

        log.info("UserForbiddenWordsChatCreateRequestDto 생성 완료");
        adminClient.insertForbiddenWordRecord(userForbiddenWordsChatCreateRequestDto);
        log.info("admin에 요청 완료");
    }
}
