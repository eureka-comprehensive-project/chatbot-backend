// package com.comprehensive.eureka.chatbot.langchain.service;
//
// import com.comprehensive.eureka.chatbot.langchain.dto.PlanRecommendationDto;
// import com.comprehensive.eureka.chatbot.langchain.dto.TelecomProfile;
// import com.comprehensive.eureka.chatbot.langchain.entity.ChatMessage;
// import com.comprehensive.eureka.chatbot.langchain.repository.ChatMessageRepository;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import dev.langchain4j.chain.ConversationalChain;
// import dev.langchain4j.memory.ChatMemory;
// import dev.langchain4j.memory.chat.TokenWindowChatMemory;
// import dev.langchain4j.model.TokenCountEstimator;
// import dev.langchain4j.model.openai.OpenAiChatModel;
// import dev.langchain4j.store.memory.chat.ChatMemoryStore;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
//
// import java.time.LocalDateTime;
// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;
//
// @Service
// @RequiredArgsConstructor
// public class ChatServiceImpl_copy2 implements ChatService {
//
//     private final OpenAiChatModel baseOpenAiModel;
//     private final ChatMessageRepository chatMessageRepository;
//     private final ChatMemoryStore memoryStore;
//     private final TokenCountEstimator tokenCountEstimator;
//     private final ObjectMapper objectMapper;
//
//     private final Map<Long, ChatMemory> userMemoryMap = new ConcurrentHashMap<>();
//     private final Map<Long, Boolean> userPromptInjected = new ConcurrentHashMap<>();
//
//     @Override
//     @Transactional
//     public String generateReply(Long userId, String message) {
//         ChatMemory memory = userMemoryMap.computeIfAbsent(userId, id ->
//                 TokenWindowChatMemory.builder()
//                         .id(id)
//                         .maxTokens(1000, tokenCountEstimator)
//                         .chatMemoryStore(memoryStore)
//                         .build()
//         );
//
//         ConversationalChain chain = ConversationalChain.builder()
//                 .chatModel(baseOpenAiModel)
//                 .chatMemory(memory)
//                 .build();
//
//         // 최초 1회만 guidancePrompt 삽입
//         if (userPromptInjected.putIfAbsent(userId, true) == null) {
//             chain.execute(guidancePrompt());
//         }
//
//         // 사용자 메시지 저장
//         saveChatMessage(userId, message, false);
//
//         // GPT 응답
//         String response = chain.execute(message);
//         saveChatMessage(userId, response, true);
//
//         // 신호 메시지 감지
//         if (response.contains("통신성향을 모두 파악했습니다")) {
//             try {
//                 String json = chain.execute(jsonExtractionPrompt());
//                 TelecomProfile profile = objectMapper.readValue(json, TelecomProfile.class);
//                 PlanRecommendationDto plan = sendToRecommendationModule(profile);
//
//                 String finalReply = String.format(
//                         "고객님께 추천드리는 요금제는 '%s'입니다. 월 %s원이며, %s 등이 포함되어 있습니다.",
//                         plan.getPlanName(), plan.getPrice(), plan.getDescription()
//                 );
//
//                 saveChatMessage(userId, finalReply, true);
//                 return finalReply;
//
//             } catch (Exception e) {
//                 return "통신성향 분석 또는 요금제 추천 중 오류가 발생했습니다. 다시 시도해 주세요.";
//             }
//         }
//
//         return response;
//     }
//
//     private void saveChatMessage(Long userId, String message, boolean isBot) {
//         ChatMessage chatMessage = new ChatMessage();
//         chatMessage.setUserId(userId);
//         chatMessage.setMessage(message);
//         chatMessage.setBot(isBot);
//         chatMessage.setTimestamp(LocalDateTime.now());
//         chatMessageRepository.save(chatMessage);
//     }
//
//     private String guidancePrompt() {
//         return """
//             당신은 고객의 통신 성향을 파악하여 맞춤 요금제를 추천하는 AI 챗봇입니다.
//
//             다음 정보를 사용자에게 질문을 통해 하나씩 파악해 주세요:
//             - 월간 데이터 사용량 (GB)
//             - 통화 시간 (분)
//             - 문자 개수
//             - 나이
//             - 성별
//             - 선호하는 부가 서비스 (예: YouTube, Netflix, Melon 등)
//
//             모든 정보를 수집했다고 판단되면, 아래 문장을 사용자에게 정확히 출력하세요:
//             "통신성향을 모두 파악했습니다. 이제 요금제를 추천해드리겠습니다."
//
//             그 다음에는 아무 말도 하지 마세요. Java 백엔드가 이후 처리를 진행합니다.
//             """;
//     }
//
//     private String jsonExtractionPrompt() {
//         return """
//             지금까지의 대화를 기반으로 사용자의 통신 성향 정보를 아래 JSON 형식에 맞게 정리해 주세요.
//             ```
//             {
//               "dataUsageGB": 15,
//               "callTimeMin": 300,
//               "smsCount": 20,
//               "age": 28,
//               "gender": "남",
//               "preferredServices": ["YouTube", "Melon"]
//             }
//             ```
//             위 형식 그대로 정확한 JSON으로만 응답하세요.
//             """;
//     }
//
//     private PlanRecommendationDto sendToRecommendationModule(TelecomProfile profile) {
//         // 실제 추천 모듈과 연동할 경우 이 부분을 HTTP POST 등으로 대체
//         PlanRecommendationDto mock = new PlanRecommendationDto();
//         mock.setPlanName("5G 시그니처 플랜");
//         mock.setPrice("59000");
//         mock.setDescription("200GB 데이터, 무제한 통화, 유튜브 프리미엄 포함");
//         return mock;
//     }
// }
