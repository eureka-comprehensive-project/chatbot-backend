package com.comprehensive.eureka.chatbot.langchain.service.impl;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ChatMemoryHandler {
    private final Map<Long, ChatMemory> userMemoryMap = new ConcurrentHashMap<>();
    private final TokenCountEstimator tokenCountEstimator;
    private final ChatMemoryStore memoryStore;
    public ChatMemory getMemoryOfChatRoom(Long chatRoomId){
        ChatMemory memory = userMemoryMap.computeIfAbsent(chatRoomId, id -> {
            TokenWindowChatMemory newMemory = TokenWindowChatMemory.builder()
                    .id(id)
                    .maxTokens(10000, tokenCountEstimator)
                    .chatMemoryStore(memoryStore)
                    .build();
            return newMemory;
        });
        return memory;
    }
}
