package com.comprehensive.eureka.chatbot.langchain.entity;

import com.comprehensive.eureka.chatbot.chatroom.entity.ChatRoom;
import io.micrometer.common.lang.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Lob
    private String message;

    private boolean isBot;

    private Long timestamp;

    @Nullable
    private boolean isRecommend;
    @Nullable
    private String recommendReason;
}