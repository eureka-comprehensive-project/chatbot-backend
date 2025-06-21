package com.comprehensive.eureka.chatbot.badword.service;

import com.comprehensive.eureka.chatbot.badword.dto.request.UserForbiddenWordsChatCreateRequestDto;
import com.comprehensive.eureka.chatbot.badword.redis.service.AllowWordRedisService;
import com.comprehensive.eureka.chatbot.badword.redis.service.ForbiddenWordRedisService;
import com.comprehensive.eureka.chatbot.client.AdminClient;
import com.comprehensive.eureka.chatbot.langchain.service.impl.BanWordValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BadwordServiceImpl implements BadwordService {

    private final ForbiddenWordRedisService forbiddenWordRedisService;
    private final AllowWordRedisService allowWordRedisService;
    private final AdminClient adminClient;

    @Override
    public Set<String> getAllForbiddendWords() {

        //redis 직접 연결

        return new HashSet<>(forbiddenWordRedisService.getAllForbiddenWords());
    }

    @Override
    public Set<String> getAllAllowedWords() {

        //redis 직접 연결

        return new HashSet<>(allowWordRedisService.getAllAllowWords());
    }

    @Override
    public boolean checkBadWord(String message){
        Set<String> badWords = getAllForbiddendWords();
        Set<String> allowedWords = getAllAllowedWords();

        BanWordValidator banWordValidator = new BanWordValidator(badWords, allowedWords);
        log.info("BanWordValidator 생성 완료");

        return banWordValidator.checkBanWord(message);
    }


    @Override
    public void sendBadwordRecord(Long userId,Long sentAt, String message){
        log.info("저장하려는,욕설이 들어간 chat의 chatMessageId : "+ message + " sentAt" + sentAt);

        Set<String> badWords = getAllForbiddendWords();
        Set<String> allowedWords = getAllAllowedWords();
        log.info("등록된 금칙어 개수" + badWords.size());
        log.info("등록된 허용어 개수" + allowedWords.size());

        BanWordValidator banWordValidator = new BanWordValidator(badWords, allowedWords);
        log.info("BanWordValidator 생성 완료");

        List<String> found = banWordValidator.findBanWords(message);
        log.info("금칙어 사용 기록 insert 중 -> 사용한 금칙어 개수 : " + found.size() + "userId : " + userId + "message : " + message );

        UserForbiddenWordsChatCreateRequestDto userForbiddenWordsChatCreateRequestDto = UserForbiddenWordsChatCreateRequestDto.builder()
                .userId(userId)
                .chatMessageText(message)
                .forbiddenWords(found)
                .sentAt(sentAt)
                .build();

        log.info("UserForbiddenWordsChatCreateRequestDto 생성 완료" + userForbiddenWordsChatCreateRequestDto.getChatMessageText() + "forbiddenwords found 의 개수"+ found.size());
        adminClient.insertForbiddenWordRecord(userForbiddenWordsChatCreateRequestDto);
        log.info("admin에 요청 완료");
    }
}
