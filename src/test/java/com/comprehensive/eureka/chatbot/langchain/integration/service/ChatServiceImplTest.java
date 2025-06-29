package com.comprehensive.eureka.chatbot.langchain.integration.service;

import com.comprehensive.eureka.chatbot.langchain.dto.ChatResponseDto;
import com.comprehensive.eureka.chatbot.langchain.service.impl.ChatServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class ChatServiceImplTest {
    @Autowired
    private ChatServiceImpl chatServiceImpl;
    @Test
    @DisplayName("심심풀이 통합 테스트")
    public void funnyChatTest() throws JsonProcessingException {
        //given
        String message = "재밌는 이야기 해줘";
        //when
        ChatResponseDto chatResponseDto = chatServiceImpl.generateReply(1L, 1L,message);
        //then
        assert(chatResponseDto != null);
    }

}
