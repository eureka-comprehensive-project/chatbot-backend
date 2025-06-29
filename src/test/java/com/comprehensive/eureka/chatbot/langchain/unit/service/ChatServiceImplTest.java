package com.comprehensive.eureka.chatbot.langchain.unit.service;


import com.comprehensive.eureka.chatbot.chatroom.entity.ChatRoom;
import com.comprehensive.eureka.chatbot.chatroom.repository.ChatRoomRepository;
import com.comprehensive.eureka.chatbot.client.UserClient;
import com.comprehensive.eureka.chatbot.client.dto.request.GetByIdRequestDto;
import com.comprehensive.eureka.chatbot.client.dto.response.GetUserProfileDetailResponseDto;
import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import com.comprehensive.eureka.chatbot.langchain.dto.ChatResponseDto;
import com.comprehensive.eureka.chatbot.langchain.entity.ChatMessage;
import com.comprehensive.eureka.chatbot.langchain.repository.ChatMessageRepository;
import com.comprehensive.eureka.chatbot.langchain.service.impl.ChatMemoryHandler;
import com.comprehensive.eureka.chatbot.langchain.service.impl.ChatServiceImpl;
import com.comprehensive.eureka.chatbot.langchain.service.impl.SentimentAnalysisService;
import com.comprehensive.eureka.chatbot.langchain.service.impl.SessionManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class ChatServiceImplTest {
    @Mock
    private OpenAiChatModel baseOpenAiModel;
    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private SentimentAnalysisService sentimentAnalysisService;

    @Mock
    private ChatMemoryHandler chatMemoryHandler;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private ChatServiceImpl chatServiceImpl;

    @Test
    public void testFunnyChat() throws JsonProcessingException {
        Long userId = 1L;
        Long chatRoomId = 1L;
        String message = "재밌는 이야기 해줘";
        chatServiceImpl.loadPrompts();
        ChatRoom chatRoom = new ChatRoom(chatRoomId, userId, LocalDateTime.now());

        // 1. chatRoomRepository.findById() mocking
        given(chatRoomRepository.findById(chatRoomId)).willReturn(Optional.of(chatRoom));

        // 2. chatMessageRepository.save() mocking (임의 ChatMessage 반환)
        given(chatMessageRepository.save(any(ChatMessage.class))).willAnswer(invocation -> invocation.getArgument(0));

        // 3. sentimentAnalysisService.analysisSentiment() mocking
        given(sentimentAnalysisService.analysisSentiment(any(String.class))).willReturn("중립");

        // 4. chatMemoryHandler.getMemoryOfChatRoom() mocking (기본 ChatMemory 객체 또는 Mockito.mock(ChatMemory.class) 사용)
        ChatMemory mockMemory = Mockito.mock(ChatMemory.class);
        given(chatMemoryHandler.getMemoryOfChatRoom(chatRoomId)).willReturn(mockMemory);

        // 5. sessionManager.getPromptProcessing() : Map<Long, Boolean> 반환 설정
        Map<Long, Boolean> promptProcessingMap = new ConcurrentHashMap<>();
        given(sessionManager.getPromptProcessing()).willReturn(promptProcessingMap);

        // 6. userClient.getUserProfile() mocking - getData() 호출 고려
        GetByIdRequestDto requestDto = new GetByIdRequestDto(userId);
        GetUserProfileDetailResponseDto mockUserProfile = Mockito.mock(GetUserProfileDetailResponseDto.class);
        BaseResponseDto<GetUserProfileDetailResponseDto> mockResponseDto = Mockito.mock(BaseResponseDto.class);

        given(userClient.getUserProfile(any(GetByIdRequestDto.class))).willReturn(mockResponseDto);
        given(mockResponseDto.getData()).willReturn(mockUserProfile);
        given(mockUserProfile.toString()).willReturn("Mocked user profile info");

        // 7. chatMessageRepository.findTopByOrderByIdDesc() mocking (return 최근 메시지 객체)
        ChatMessage lastChatMessage = new ChatMessage();
        lastChatMessage.setId(999L);
        given(chatMessageRepository.findTopByOrderByIdDesc()).willReturn(lastChatMessage);

        // Optional) mockMemory 관련 메서드 clear(), add(...) 호출 허용
        doNothing().when(mockMemory).clear();
        doNothing().when(mockMemory).add(any());

        // --- 이제 실제 테스트 메서드 호출 가능 ---
        ChatResponseDto responseDto = chatServiceImpl.generateReply(userId, chatRoomId, message);

        // assert, verify 등 테스트 작성
        assertNotNull(responseDto);
    }
//    @Test
//    public void funnyChatTest() throws JsonProcessingException {
//        //given
//        String message = "재밌는 이야기 해줘";
////        ChatRoom chatRoom = new ChatRoom(1L,1L, LocalDateTime.now());
////        ChatMessage chatMessage = ChatMessage.builder()
////                .userId(1L)
////                .chatRoom(chatRoom)
////                .message(message)
////                .isBot(false)
////                .isRecommend(false)
////                .isPlanShow(false)
////                .recommendReason("mock reason")
////                .build();
//
////        given(chatMemoryHandler.getMemoryOfChatRoom(1L)).willReturn(memory);
////        given(sentimentAnalysisService.analysisSentiment(any(String.class))).willReturn("중립");
////        given(chatRoomRepository.findById(1L)).willReturn(Optional.of(chatRoom));
//////        given(chatMessageRepository.save(chatMessage)).willReturn(chatMessage);
////        given(chatMessageRepository.save(any(ChatMessage.class))).willReturn(chatMessage);
//
//        //when
//        ChatResponseDto chatResponseDto = chatServiceImpl.generateReply(1L, 1L,message);
//        //then
//        assert(chatResponseDto != null);
//    }

}
