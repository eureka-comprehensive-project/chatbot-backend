package com.comprehensive.eureka.chatbot.gemini.config;

import com.comprehensive.eureka.chatbot.gemini.service.GeminiTelecomAssistant;
import com.comprehensive.eureka.chatbot.gemini.service.tool.GeminiRecommendationOrchestratorService;
import com.comprehensive.eureka.chatbot.gemini.service.tool.GeminiUserInfoService;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.google.GoogleAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Profile; // 필요시

@Configuration
// @Profile("gemini") // 특정 프로필에서만 활성화하려면 주석 해제
public class GeminiLangChainConfig {

    @Value("${langchain4j.google-ai.api-key}")
    private String apiKey;

    @Value("${langchain4j.google-ai.model-name}")
    private String modelName;

    @Value("${langchain4j.google-ai.temperature:0.7}") // 기본값 설정
    private Double temperature;

    @Value("${langchain4j.google-ai.max-output-tokens:1024}") // 기본값 설정
    private Integer maxOutputTokens;

    @Value("${langchain4j.google-ai.top-k:#{null}}") // 기본값 null (설정 안 함)
    private Integer topK;

    @Value("${langchain4j.google-ai.top-p:#{null}}") // 기본값 null (설정 안 함)
    private Double topP;

    @Bean("geminiChatModel")
    public ChatLanguageModel chatLanguageModel() {
        GoogleAiChatModel.Builder builder = GoogleAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature)
                .maxOutputTokens(maxOutputTokens);

        if (topK != null) {
            builder.topK(topK);
        }
        if (topP != null) {
            builder.topP(topP);
        }
        // 추가적인 설정이 있다면 여기에 추가
        // .responseMimeType("application/json") // JSON 모드 사용 시 (모델 지원 여부 확인)
        return builder.build();
    }

    @Bean("geminiChatMemoryStore")
    public ChatMemoryStore chatMemoryStore() {
        return new InMemoryChatMemoryStore();
    }

    @Bean("geminiChatMemoryProvider")
    public ChatMemoryProvider chatMemoryProvider(@Qualifier("geminiChatMemoryStore") ChatMemoryStore chatMemoryStore) {
        return userId -> MessageWindowChatMemory.builder()
                .id(userId.toString()) // LangChain4j는 String ID를 사용
                .maxMessages(20) // 예: 최근 20개 메시지 기억 (조정 가능)
                .chatMemoryStore(chatMemoryStore)
                .build();
    }

    @Bean
    public GeminiTelecomAssistant geminiTelecomAssistant(@Qualifier("geminiChatModel") ChatLanguageModel chatLanguageModel,
                                                         @Qualifier("geminiChatMemoryProvider") ChatMemoryProvider chatMemoryProvider,
                                                         GeminiUserInfoService geminiUserInfoService, // 자동 주입
                                                         GeminiRecommendationOrchestratorService geminiRecommendationOrchestratorService) { // 자동 주입
        return AiServices.builder(GeminiTelecomAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(chatMemoryProvider)
                .tools(geminiUserInfoService, geminiRecommendationOrchestratorService)
                // .timeout(Duration.ofSeconds(60)) // 필요시 타임아웃 설정
                // .retryStrategy(...) // 필요시 재시도 전략 설정
                .build();
    }
}