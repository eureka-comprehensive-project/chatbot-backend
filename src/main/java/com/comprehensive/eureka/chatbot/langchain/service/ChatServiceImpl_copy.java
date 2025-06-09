// package com.comprehensive.eureka.chatbot.langchain.service;
//
// import com.comprehensive.eureka.chatbot.langchain.dto.PlanRecommendationDto;
// import com.comprehensive.eureka.chatbot.langchain.dto.TelecomProfile;
// import com.comprehensive.eureka.chatbot.langchain.entity.ChatMessage;
// import com.comprehensive.eureka.chatbot.langchain.repository.ChatMessageRepository;
// import com.fasterxml.jackson.databind.JsonNode;
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
// public class ChatServiceImpl_copy implements ChatService {
//
//     private final OpenAiChatModel baseOpenAiModel;
//     private final ChatMessageRepository chatMessageRepository;
//     private final ChatMemoryStore memoryStore;
//     private final TokenCountEstimator tokenCountEstimator;
//     private final ObjectMapper objectMapper;
//
//     private final Map<Long, ChatMemory> userMemoryMap = new ConcurrentHashMap<>();
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
//         // 사용자 메시지 저장
//         saveChatMessage(userId, message, false);
//
//         // 사용자의 발화가 요금제 추천 요청인지 판단 (GPT 기반)
//         if (isPlanRecommendationIntent(message, chain)) {
//             injectGuidancePrompt(chain); // 질문 유도용 프롬프트 삽입
//         }
//
//         // GPT 응답 생성
//         String response = chain.execute(message);
//         System.out.println("[BOT RESPONSE] " + response);
//
//         // JSON이 감지되면 → 통신성향 분석 → 요금제 추천
//         if (isValidJsonProfile(response)) {
//             try {
//                 TelecomProfile profile = objectMapper.readValue(response, TelecomProfile.class);
//                 PlanRecommendationDto plan = sendToRecommendationModule(profile);
//
//                 String finalReply = String.format(
//                         "고객님의 통신 성향에 맞는 추천 요금제는 '%s'입니다. 월 %s원이며, %s 등의 혜택이 포함됩니다.",
//                         plan.getPlanName(),
//                         plan.getPrice(),
//                         plan.getDescription()
//                 );
//
//                 saveChatMessage(userId, finalReply, true);
//                 return finalReply;
//
//             } catch (Exception e) {
//                 System.out.println("⚠️ JSON 파싱 실패: " + e.getMessage());
//                 // GPT 응답 그대로 반환
//             }
//         }
//
//         // 일반 응답 저장
//         saveChatMessage(userId, response, true);
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
//     // 사용자의 발화가 요금제 추천 요청인지 판단 (GPT에게 위임)
//     private boolean isPlanRecommendationIntent(String userMessage, ConversationalChain chain) {
//         String prompt = String.format(
//                 """
//                 사용자의 다음 메시지가 요금제 추천이나 상품 가입 의도와 관련된 것인지 판단해줘.
//                 단순히 '예' 또는 '아니오'로만 대답해줘.
//
//                 메시지: "%s"
//                 """, userMessage
//         );
//
//         String result = chain.execute(prompt).trim().toLowerCase();
//         return result.contains("예") || result.contains("yes") || result.contains("true");
//     }
//
//     // 통신 성향 질문 유도용 프롬프트 삽입
//     private void injectGuidancePrompt(ConversationalChain chain) {
//         String guidancePrompt = """
//             사용자가 요금제를 추천받거나 상품 가입을 원하는 경우,
//             통신 성향 파악을 위해 아래 정보를 하나씩 질문을 통해 수집하세요:
//
//             - 월 데이터 사용량 (GB)
//             - 통화 시간 (분)
//             - 문자 개수
//             - 나이
//             - 성별
//             - 선호하는 부가 서비스 (예: YouTube, Netflix, Melon 등)
//
//             모든 정보가 수집되면 아래 JSON 형식으로 응답하세요. 그 전에는 절대 JSON을 출력하지 마세요:
//
//             {
//               "dataUsageGB": 15,
//               "callTimeMin": 300,
//               "smsCount": 20,
//               "age": 28,
//               "gender": "남",
//               "preferredServices": ["YouTube", "Melon"]
//             }
//             """;
//         chain.execute(guidancePrompt);
//     }
//
//     // 응답이 JSON 형식인지 감지
//     private boolean isValidJsonProfile(String text) {
//         try {
//             JsonNode node = objectMapper.readTree(text);
//             return node.has("dataUsageGB") &&
//                     node.has("callTimeMin") &&
//                     node.has("smsCount") &&
//                     node.has("age") &&
//                     node.has("gender") &&
//                     node.has("preferredServices");
//         } catch (Exception e) {
//             return false;
//         }
//     }
//
//     // (임시) 요금제 추천 모듈 응답 생성
//     private PlanRecommendationDto sendToRecommendationModule(TelecomProfile profile) {
//         PlanRecommendationDto mock = new PlanRecommendationDto();
//         mock.setPlanName("5G 시그니처 플랜");
//         mock.setPrice("59000");
//         mock.setDescription("200GB 데이터, 무제한 통화, 유튜브 프리미엄 포함");
//         return mock;
//     }
// }
