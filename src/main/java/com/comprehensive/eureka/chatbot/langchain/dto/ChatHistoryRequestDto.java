package com.comprehensive.eureka.chatbot.langchain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoryRequestDto {
    private Long chatRoomId;
    private Long userId;
    private Long lastMessageId;
    private int pageSize = 10;
}
