package com.comprehensive.eureka.chatbot.langchain.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDto {
    private Long userId;
    private Long chatRoomId;
    private String message;

}
