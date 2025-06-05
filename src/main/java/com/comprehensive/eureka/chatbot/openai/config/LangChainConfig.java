package com.comprehensive.eureka.chatbot.config;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChainConfig {

    @Value("${openai.api.key}")
    private String openAiKey;

    @Bean
    public OpenAiChatModel openAiChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(openAiKey)
                .modelName("gpt-3.5-turbo")
                .build();
    }

    @Bean
    public ChatMemoryStore memoryStore() {
        return new InMemoryChatMemoryStore();
    }

    @Bean
    public TokenCountEstimator tokenCountEstimator() {
        return new OpenAiTokenCountEstimator("gpt-3.5-turbo");
    }

    @Bean
    public ChatMemoryProvider chatMemoryProvider(ChatMemoryStore memoryStore,
                                                 TokenCountEstimator estimator) {
        return userId -> TokenWindowChatMemory.builder()
                .id(userId)
                .maxTokens(1000, estimator)
                .chatMemoryStore(memoryStore)
                .build();
    }
}
