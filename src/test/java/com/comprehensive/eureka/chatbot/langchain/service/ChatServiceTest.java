package com.comprehensive.eureka.chatbot.langchain.service;
import com.comprehensive.eureka.chatbot.badword.service.BadwordServiceImpl;
import com.comprehensive.eureka.chatbot.chatroom.entity.ChatRoom;
import com.comprehensive.eureka.chatbot.chatroom.repository.ChatRoomRepository;
import com.comprehensive.eureka.chatbot.client.UserClient;
import com.comprehensive.eureka.chatbot.client.dto.request.GetByIdRequestDto;
import com.comprehensive.eureka.chatbot.client.dto.response.GetUserProfileDetailResponseDto;
import com.comprehensive.eureka.chatbot.langchain.dto.ChatMessageDto;
import com.comprehensive.eureka.chatbot.langchain.dto.ChatResponseDto;
import com.comprehensive.eureka.chatbot.langchain.entity.ChatMessage;
import com.comprehensive.eureka.chatbot.langchain.repository.ChatMessageRepository;
import com.comprehensive.eureka.chatbot.langchain.service.impl.ChatServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private UserClient userClient;
    @Mock
    private BadwordServiceImpl badwordServiceImpl;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatServiceImpl chatServiceImpl;

    @Test
    @DisplayName("사용자 정보를 조회 할 수 있다.")
    void testUserInfoChat() throws JsonProcessingException {
        // given
        GetByIdRequestDto getByIdRequestDto = GetByIdRequestDto.builder()
                .id(1L)
                .build();
        GetUserProfileDetailResponseDto getUserProfileDetailResponseDto = new GetUserProfileDetailResponseDto(
                "hong@naver.com",
                "홍길동",
                "010-0000-0000",
                LocalDate.of(1999, 1, 1),
                LocalDateTime.now()
        );
        ChatRoom testChatRoom = new ChatRoom(1L, 1L,LocalDateTime.now());
        String message = "사용자 조회 할래";
        ChatMessage chatMessage = ChatMessage.builder()
                .userId(1L)
                .chatRoom(new ChatRoom(1L, 1L,LocalDateTime.now()))
                .message("사용자 조회 할래")
                .isBot(false)
                .isRecommend(false)
                .isPlanShow(false)
                .recommendReason("mockReason")
                .build();
        long unixTimestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        chatMessage.setTimestamp(unixTimestamp);

        given(userClient.getUserProfile(getByIdRequestDto))
                .willReturn((BaseResponseDto.success(getUserProfileDetailResponseDto)));
        given(badwordServiceImpl.checkBadWord(any(String.class)))
                .willReturn((true));
        given(chatRoomRepository.findById(1L))
                .willReturn(Optional.of(testChatRoom));
        given(chatMessageRepository.save(any(ChatMessage.class)))
                .willReturn(chatMessage);
        given(chatMessageRepository.save(chatMessage))
                .willReturn(new ChatMessage());
        // when
        ChatResponseDto chatResponseDto = chatServiceImpl.generateReply(1L, 1L, "사용자 조회 할래");

        // then
        assertThat(chatResponseDto.getMessage()).isNotEmpty();
    }

    private ChatMessageDto saveChatMessage(Long userId, ChatRoom chatRoom, String message, boolean isBot, boolean isRecommend, boolean isPlanShow, String recommendReason) {
        // LocalDateTime -> 유닉스 타임스탬프 (초 단위)
        long unixTimestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();

        ChatMessage chatMessage = ChatMessage.builder()
                .userId(userId)
                .chatRoom(chatRoom)
                .message(message)
                .isBot(isBot)
                .isRecommend(isRecommend)
                .isPlanShow(isPlanShow)
                .recommendReason(recommendReason)
                .build();

        chatMessage.setTimestamp(unixTimestamp);

        ChatMessage chat = chatMessageRepository.save(chatMessage);
        ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                .message(chat.getMessage())
                .timestamp(chat.getTimestamp())
                .messageId(chat.getId())
                .build();

        return chatMessageDto;
    }
}
