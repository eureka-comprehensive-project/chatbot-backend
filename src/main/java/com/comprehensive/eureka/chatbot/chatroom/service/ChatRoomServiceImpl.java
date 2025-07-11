package com.comprehensive.eureka.chatbot.chatroom.service;

import com.comprehensive.eureka.chatbot.chatroom.dto.ChatRoomInfoDto;
import com.comprehensive.eureka.chatbot.chatroom.dto.ChatRoomListResponseDto;
import com.comprehensive.eureka.chatbot.chatroom.dto.CreateChatRoomResponseDto;
import com.comprehensive.eureka.chatbot.chatroom.entity.ChatRoom;
import com.comprehensive.eureka.chatbot.chatroom.repository.ChatRoomRepository;
import com.comprehensive.eureka.chatbot.langchain.entity.ChatMessage;
import com.comprehensive.eureka.chatbot.langchain.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public ChatRoomListResponseDto getChatRoomList(Long userId, Long chatRoomId, int size) {
        log.info("[getChatRoomList] 채팅방 목록 요청 - userId: {}, lastChatRoomId: {}, size: {}", userId, chatRoomId, size);

        // 1개 더 가져오기
        Pageable pageable = PageRequest.of(0, size + 1, Sort.by(Sort.Direction.DESC, "chatRoomId"));
        List<ChatRoom> chatRooms = chatRoomRepository.findByUserIdWithPaging(userId, chatRoomId, pageable);

        // 실제로 응답할 목록은 size까지만 자름
        List<ChatRoom> displayRooms = chatRooms.stream().limit(size).toList();

        // chatRoomIds 추출
        List<Long> chatRoomIds = displayRooms.stream()
                .map(ChatRoom::getChatRoomId)
                .collect(Collectors.toList());

        // 채팅방별 첫 메시지 한 번에 조회
        List<ChatMessage> messages = chatMessageRepository.findFirstUserMessagesByChatRoomIds(userId, chatRoomIds);

        // Map<chatRoomId, message>로 변환
        Map<Long, String> firstMessageMap = messages.stream()
                .collect(Collectors.toMap(
                        msg -> msg.getChatRoom().getChatRoomId(),
                        ChatMessage::getMessage
                ));

        // ChatRoomInfoDto 만들기
        List<ChatRoomInfoDto> chatRoomInfoList = displayRooms.stream()
                .map(room -> ChatRoomInfoDto.builder()
                        .chatRoomId(room.getChatRoomId())
                        .userId(room.getUserId())
                        .createdAt(room.getCreatedAt())
                        .firstMessage(firstMessageMap.getOrDefault(room.getChatRoomId(), ""))
                        .build())
                .collect(Collectors.toList());

        boolean hasNext = chatRooms.size() > size;

        return ChatRoomListResponseDto.builder()
                .chatRooms(chatRoomInfoList)
                .hasNext(hasNext)
                .build();
    }

    @Override
    public CreateChatRoomResponseDto createChatRoom(Long userId) {
        log.info("[createChatRoom] 채팅방 생성 요청 - userId: {}", userId);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setUserId(userId);
        chatRoom.setCreatedAt(LocalDateTime.now());

        ChatRoom saved = chatRoomRepository.save(chatRoom);

        return CreateChatRoomResponseDto.builder()
                .chatRoomId(saved.getChatRoomId())
                .createdAt(saved.getCreatedAt())
                .build();
    }
}
