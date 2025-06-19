package com.comprehensive.eureka.chatbot.langchain.dto;

import com.comprehensive.eureka.chatbot.chatroom.entity.ChatRoom;
import io.micrometer.common.lang.Nullable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class ChatMessageDto {

    private Long messageId;
    private String message;
    private Long timestamp;

}
