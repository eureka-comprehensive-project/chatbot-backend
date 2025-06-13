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
    private final ObjectMapper objectMapper;

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
            badWordService.sendBadwordRecord(userId,chatMessageId,message);
            return "부적절한 표현이 감지되어 답변할 수 없습니다. 히히";
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
        return """
            당신은 고객의 통신 성향을 파악하여 맞춤 요금제를 추천하는 AI 챗봇 중에서, 고객의 통신 성향을 파악하기 위해 대화를 이어나가는 역할을 담당합니다.

            다음 정보를 사용자에게 질문을 통해 하나씩 파악해 주세요:
            - 월간 데이터 사용량 (GB)
            - 통화 시간 (분)
            - 문자 개수
            - 나이
            - 성별
            - 선호하는 부가 서비스 (예: YouTube, Netflix, Melon 등)

            모든 정보를 수집했다고 판단되면, 아래 문장을 사용자에게 정확히 출력하세요:
            "통신성향을 모두 파악했습니다. 이제 요금제를 추천해드리겠습니다."

            그 다음에는 아무 말도 하지 마세요. Java 백엔드가 이후 처리를 진행합니다.
        """;
    }

    private String jsonExtractionPrompt() {
        return """
            지금까지의 대화를 기반으로 사용자의 통신 성향 정보를 아래 JSON 형식에 맞게 정리해 주세요.
            
            - 모든 항목은 정확한 숫자(int) 또는 문자열 형식에 맞춰 작성해 주세요.
            - 문자 개수(smsCount)가 '무제한'일 경우 반드시 숫자 99999로 작성해 주세요.
            - 필요한 데이터 사용량(dataUsageGB)도 무제한일 경우 숫자 99999로 표현하세요.
            - 통화 시간(callTimeMin)도 무제한일 경우 숫자 99999로 표현하세요.
            - gender는 반드시 "남" 또는 "여" 중 하나의 문자열이어야 합니다.
            - preferredServices는 문자열 배열로 작성하세요.
            
            아래 형식 그대로 JSON으로만 응답하세요. 설명은 필요 없습니다.
            
            {
              "dataUsageGB": 15,
              "callTimeMin": 300,
              "smsCount": 20,
              "age": 28,
              "gender": "남",
              "preferredServices": ["YouTube", "Melon"]
            }
            """;
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
