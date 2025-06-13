package com.comprehensive.eureka.chatbot.langchain.service;

import com.comprehensive.eureka.chatbot.badword.service.BadwordServiceImpl;
import com.comprehensive.eureka.chatbot.langchain.dto.PlanRecommendationDto;
import com.comprehensive.eureka.chatbot.langchain.dto.TelecomProfile;
import com.comprehensive.eureka.chatbot.langchain.repository.ChatMessageRepository;
import com.comprehensive.eureka.chatbot.langchain.entity.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
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
    private final ObjectMapper objectMapper;

    private String systemPrompt;
    private String jsonExtractionPrompt;

    @PostConstruct
    public void loadPrompts() {
        try {
            Resource systemResource = new ClassPathResource("prompts/system-prompt.txt");
            try (InputStream in = systemResource.getInputStream()) {
                this.systemPrompt = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }

            Resource jsonResource = new ClassPathResource("prompts/json-extraction-prompt.txt");
            try (InputStream in = jsonResource.getInputStream()) {
                this.jsonExtractionPrompt = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("프롬프트 로드 중 오류 발생", e);
        }
    }

    // 사용자별 메모리 관리
    private final Map<Long, ChatMemory> userMemoryMap = new ConcurrentHashMap<>();
    private final BadwordServiceImpl badWordService;
    @Override
    @Transactional
    public String generateReply(Long userId, String message) {
        ChatMemory memory = userMemoryMap.computeIfAbsent(userId, id -> {
            TokenWindowChatMemory newMemory = TokenWindowChatMemory.builder()
                    .id(id)
                    .maxTokens(1000, tokenCountEstimator)
                    .chatMemoryStore(memoryStore)
                    .build();

            // System 역할 명확하게 지정
            newMemory.add(SystemMessage.from(systemPrompt()));
            return newMemory;
        });

        boolean isBad = false;
        try{
            if(badWordService.checkBadWord(message)){
                isBad = true;
            }
        }catch(Exception e){
            return "부적절한 표현 감지 중 에러 발생";
        }


        ConversationalChain chain = ConversationalChain.builder()
                .chatModel(baseOpenAiModel)
                .chatMemory(memory)
                .build();

        // 사용자 메시지 저장
        saveChatMessage(userId, message, false);
        if(isBad){
            Long chatMessageId = chatMessageRepository.findTopByOrderByIdDesc().getId();
            try{
                badWordService.sendBadwordRecord(userId,chatMessageId,message);
            }catch(Exception e){
                return("지금 현재 admin 모듈의 금칙어와 chatbot모듈의 금칙어가 동기화돼있지 않아, 기록을 남길 수 없습니다. admin 모듈에서 해당 단어를 추가한 후에 다시 시도하세요");
            }

            return "부적절한 표현이 감지되어 답변할 수 없습니다.";
        }
        // GPT 응답
        String response = chain.execute(message);
        saveChatMessage(userId, response, true);

        // 통신성향 수집 완료 신호 감지
        if (response.contains("통신성향을 모두 파악했습니다")) {
            try {
                // JSON 추출용 프롬프트 메시지 삽입후, JSON 추출
                String json = chain.execute(jsonExtractionPrompt());
                System.out.println("[DEBUG] Extracted JSON from GPT:\n" + json);

                TelecomProfile profile = objectMapper.readValue(json, TelecomProfile.class);

                // 요금제 추천 모듈로 JSON 보내고, 추천 요금제 받아오기
                PlanRecommendationDto plan = sendToRecommendationModule(profile);

                String finalReply = String.format(
                        "고객님께 추천드리는 요금제는 '%s'입니다. 월 %s원이며, %s 등이 포함되어 있습니다.",
                        plan.getPlanName(), plan.getPrice(), plan.getDescription()
                );

                saveChatMessage(userId, finalReply, true);
                return finalReply;
            } catch (Exception e) {
                return "통신성향 분석 또는 요금제 추천 중 오류가 발생했습니다. 다시 시도해 주세요.";
            }
        }

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

    private String systemPrompt() {
        return systemPrompt;
    }

    private String jsonExtractionPrompt() {
        return jsonExtractionPrompt;
    }

    private PlanRecommendationDto sendToRecommendationModule(TelecomProfile profile) {
        // 실제 추천 모듈과 연동할 경우 이 부분을 HTTP POST 등으로 대체
        PlanRecommendationDto mock = new PlanRecommendationDto();
        mock.setPlanName("5G 시그니처 플랜");
        mock.setPrice("59000");
        mock.setDescription("200GB 데이터, 무제한 통화, 유튜브 프리미엄 포함");
        return mock;
    }
}
