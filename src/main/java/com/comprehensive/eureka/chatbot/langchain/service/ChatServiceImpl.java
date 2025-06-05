package com.comprehensive.eureka.chatbot.langchain.service;

import com.comprehensive.eureka.chatbot.langchain.repository.ChatMessageRepository;
import com.comprehensive.eureka.chatbot.langchain.entity.ChatMessage;
import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final OpenAiChatModel baseOpenAiModel;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMemoryStore memoryStore;
    private final TokenCountEstimator tokenCountEstimator;

    private final Map<Long, ChatMemory> userMemoryMap = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public String generateReply(Long userId, String message) {
        ChatMemory memory = userMemoryMap.computeIfAbsent(userId, id ->
                TokenWindowChatMemory.builder()
                        .id(id)
                        .maxTokens(1000, tokenCountEstimator)
                        .chatMemoryStore(memoryStore)
                        .build()
        );

        ConversationalChain chain = ConversationalChain.builder()
                .chatModel(baseOpenAiModel)
                .chatMemory(memory)
                .build();

        String response = chain.execute(message);

        System.out.println("[USER] " + userId + ": " + message);
        System.out.println("[BOT] → " + response);

        saveChatMessage(userId, message, false);
        saveChatMessage(userId, response, true);

        return response;
    }

    private void saveChatMessage(Long userId, String message, boolean isBot) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUserId(userId);
        chatMessage.setMessage(message);
        chatMessage.setBot(isBot);
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(chatMessage);
    }
}
