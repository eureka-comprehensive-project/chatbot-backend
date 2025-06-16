package com.comprehensive.eureka.chatbot.langchain.service;

import com.comprehensive.eureka.chatbot.badword.service.BadwordServiceImpl;
import com.comprehensive.eureka.chatbot.client.RecommendClient;
import com.comprehensive.eureka.chatbot.client.SentimentClient;
import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import com.comprehensive.eureka.chatbot.langchain.dto.*;
import com.comprehensive.eureka.chatbot.langchain.dto.UserPreferenceDto;
import com.comprehensive.eureka.chatbot.langchain.entity.ChatMessage;
import com.comprehensive.eureka.chatbot.langchain.repository.ChatMessageRepository;
import com.comprehensive.eureka.chatbot.prompt.service.PromptServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.ZoneId;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final OpenAiChatModel baseOpenAiModel;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMemoryStore memoryStore;
    private final TokenCountEstimator tokenCountEstimator;
    private final ObjectMapper objectMapper;
    private final RecommendClient recommendClient;
    private final SentimentClient sentimentClient;
    private String recommendPrompt;
    private String userInfoPrompt;
    private String funnyChatPrompt;
    private String whatTodoPrompt;
    private String jsonExtractionPrompt;
    private String keywordExtractionPrompt;

    @PostConstruct
    public void loadPrompts() {
        try {
            Resource systemResource = new ClassPathResource("prompts/system-prompt.txt");
            try (InputStream in = systemResource.getInputStream()) {
                this.recommendPrompt = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }

            Resource systemResource2 = new ClassPathResource("prompts/infoChat-prompt.txt");
            try (InputStream in = systemResource2.getInputStream()) {
                this.userInfoPrompt = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }

            Resource systemResource3 = new ClassPathResource("prompts/funnyChat-prompt.txt");
            try (InputStream in = systemResource3.getInputStream()) {
                this.funnyChatPrompt = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }

            Resource systemResource4 = new ClassPathResource("prompts/determine-whattodo-prompt.txt");
            try (InputStream in = systemResource4.getInputStream()) {
                this.whatTodoPrompt = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }

            Resource jsonResource = new ClassPathResource("prompts/json-extraction-prompt.txt");
            try (InputStream in = jsonResource.getInputStream()) {
                this.jsonExtractionPrompt = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }

            Resource keywordResource = new ClassPathResource("prompts/keyword-prompt.txt");
            try (InputStream in = keywordResource.getInputStream()) {
                this.keywordExtractionPrompt = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("프롬프트 로드 중 오류 발생", e);
        }
    }
    // 사용자별 메모리 관리
    private final Map<Long, ChatMemory> userMemoryMap = new ConcurrentHashMap<>();
    private final Map<Long, Boolean> promptProcessing = new ConcurrentHashMap<>();
    private final Map<Long, Boolean> firstChatActivated = new ConcurrentHashMap<>();

    private final BadwordServiceImpl badWordService;
    private final PromptServiceImpl promptService;

    @Override
    @Transactional
    public String generateReply(Long userId, String message) {

        //감정 분석
        log.info("감정 분석 시작");
        String sentimentJson = sentimentClient.determineSentiment(new DetermineSentimentDto(message));
        String sentiment = "";
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            sentiment = objectMapper.readTree(sentimentJson).get("sentiment").asText();
        }catch(Exception e){
            return "감정 분석 결과 parsing 실패";
        }


        log.info("감정 분석 결과: " + sentiment);

        //사용자 마다 다른 memory 할당
        ChatMemory memory = userMemoryMap.computeIfAbsent(userId, id -> {
            TokenWindowChatMemory newMemory = TokenWindowChatMemory.builder()
                    .id(id)
                    .maxTokens(10000, tokenCountEstimator)
                    .chatMemoryStore(memoryStore)
                    .build();

            return newMemory;
        });
        promptProcessing.putIfAbsent(userId, false);
        firstChatActivated.putIfAbsent(userId, true);

        //만약 한 prompt의 로직이 종료 됐다면, prompt를 갈아 끼는 모드로 변경
        if (!promptProcessing.get(userId) || firstChatActivated.get(userId)){
            System.out.println("isProcessing false");
            firstChatActivated.put(userId,false);
            memory.add(SystemMessage.from(whatTodoPrompt));
        }


        //설정한 prompt로 저장된 memory 대로 lang chain 생성
        ConversationalChain chain = ConversationalChain.builder()
                .chatModel(baseOpenAiModel)
                .chatMemory(memory)
                .build();

        // 사용자 메시지 저장
        saveChatMessage(userId, message, false);

        //금칙어 포함 시 금칙어 사용 기록에 저장 ( admin 모듈 ) 후 처리
        try {
            if (badWordService.checkBadWord(message)) {
//                saveForbiddenWordRecord(userId,message);
                return "부적절한 표현이 감지되어 답변할 수 없습니다.";
            }
        } catch (Exception e) {
            return "지금 현재 admin 모듈의 금칙어와 chatbot 모듈의 금칙어가 동기화돼있지 않아, 기록을 남길 수 없습니다. admin 모듈에서 해당 단어를 추가한 후에 다시 시도하세요";
        }

        // TODO

        // GPT 응답
        String response = chain.execute(message);
        saveChatMessage(userId, response, true);

        //매 답변마다의 감정코드에 맞는 chatbot의 태도 추출
//        String attitude = promptService.getPromptBySentimentName(sentiment).getScenario();
        String attitude = "정보 제공성 말투";
        //매 답변마다 혹시 prompt 전환여지가 있었는지 분석
        if(response.contains("[prompt전환]1번으로 예상")){
            System.out.println("[prompt전환]1번으로 예상");
            memory.add(SystemMessage.from(userInfoPrompt + attitude));
            promptProcessing.put(userId,true); //해당 prompt가 계속 진행되게 한다.
            response = chain.execute(message);
        }else if(response.contains("[prompt전환]2번으로 예상")){
            System.out.println("[prompt전환]2번으로 예상");
            memory.add(SystemMessage.from(funnyChatPrompt + attitude));
            promptProcessing.put(userId,true);
            response = chain.execute(message);
        }else if(response.contains("[prompt전환]3번으로 예상")) {
            System.out.println("[prompt전환]3번으로 예상");
            memory.add(SystemMessage.from(recommendPrompt + attitude));
            promptProcessing.put(userId, true);
            response = chain.execute(message);
        }else if(response.contains("[prompt전환]4번으로 예상")){
            System.out.println("[prompt전환]4번으로 예상");
            memory.add(SystemMessage.from("못알아 들었습니다 라고 한다" + attitude));
            promptProcessing.put(userId, false);
            response = chain.execute(message);
        }

        //사용자 정보 제공 완료 감지
        if(response.contains("사용자 정보 제공 준비 끝")){
            //현재 구현 안되어 있음. todo
            //todo : 사용자 정보 api 호출후 제공
            promptProcessing.put(userId,false);
            System.out.println("사용자 정보 제공 끝");
            response = "저랑 무엇을 하길 원하나요? 요금제 추천, 사용자 정보 알기, 심심풀이 중 고르세요";
        }

        //심심풀이 완료 감지
        if(response.contains("재밌는 이야기 였습니다")){
            promptProcessing.put(userId,false);
            System.out.println("심심풀이 끝");
            response = "저랑 무엇을 하길 원하나요? 요금제 추천, 사용자 정보 알기, 심심풀이 중 고르세요";
        }


        if (response.contains("직업을 확인하였습니다") || response.contains("키워드를 확인하였습니다")) {
            try {
                String extractedKeyword = null;
                final int MAX_RETRIES = 2;
                int attempt = 0;
                boolean validKeyword = false;

                while (attempt < MAX_RETRIES) {
                    extractedKeyword = chain.execute(keywordExtractionPrompt);
                    if (extractedKeyword != null && !extractedKeyword.isBlank()) {
                        validKeyword = true;
                        break;
                    }
                    attempt++;
                }

                if (!validKeyword) {
                    return "키워드 추출 중 오류가 발생했습니다. 다시 시도해 주세요.";
                }


                log.info("extractedKeyword : {}", extractedKeyword);
                List<RecommendPlanDto> recommendPlans = sendKeywordToRecommendationModule(extractedKeyword);
                if (recommendPlans == null || recommendPlans.isEmpty()) {
                    return "추천드릴 요금제를 찾지 못했습니다. 다른 키워드로 다시 시도해 주세요.";
                }

                String finalReply = "고객님께 다음 요금제들을 추천해 드립니다.\n\n" +
                        recommendPlans.stream()
                                .map(recommend -> {
                                    PlanDto plan = recommend.getPlan();
                                    return String.format(
                                            "요금제: '%s'\n- 월정액: %s원\n- 제공량: %s %s (통화량: %s분)\n",
                                            plan.getPlanName(),
                                            plan.getMonthlyFee(),
                                            plan.getDataAllowance(),
                                            plan.getDataAllowanceUnit(),
                                            plan.getAdditionalCallAllowance()
                                    );
                                })
                                .collect(Collectors.joining("\n"));

                saveChatMessage(userId, finalReply, true);
                return finalReply;

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return "키워드 기반 요금제 추천 중 오류가 발생했습니다. 다시 시도해 주세요. 또 저랑 무엇을 하길 원하나요? 요금제 추천, 사용자 정보 알기, 심심풀이 중 고르세요";
            }
        }
        // 통신성향 수집 완료 신호 감지
        if (response.contains("통신성향을 모두 파악했습니다")) {
            try {
                promptProcessing.put(userId,false);
                JsonNode root = null;
                String rawJson;
                final int MAX_RETRIES = 2;
                int attempt = 0;
                boolean valid = false;

                while (attempt < MAX_RETRIES) {
                    rawJson = chain.execute(jsonExtractionPrompt);
                    log.info("rawJson : {}", rawJson);
                    root = objectMapper.readTree(rawJson);
                    log.info("root : {}", root);
                    // UserPreferenceDto 필드 유효성 검사
                    if (root.get("preferenceDataUsage").isInt()
                            && root.get("preferenceDataUsageUnit").isTextual()
                            && root.get("preferenceSharedDataUsage").isInt()
                            && root.get("preferenceSharedDataUsageUnit").isTextual()
                            && root.get("preferencePrice").isInt()
                            && root.get("preferenceBenefitGroupId").isInt()
                            && root.get("isPreferenceFamilyData").isBoolean()
                            && root.get("preferenceValueAddedCallUsage").isInt()) {
                        valid = true;
                        break;
                    }
                    attempt++;
                }

                if (!valid) {
                    return "통신성향 분석 또는 요금제 추천 중 오류가 발생했습니다. 다시 시도해 주세요. 또 저랑 무엇을 하길 원하나요? 요금제 추천, 사용자 정보 알기, 심심풀이 중 고르세요";
                }

                UserPreferenceDto preference =objectMapper.treeToValue(root, UserPreferenceDto.class);

                log.info("preference : {}", preference);
                RecommendationResponseDto recommendationResponse = sendToRecommendationModule(preference, userId);
                List<RecommendPlanDto> recommendPlans = recommendationResponse.getRecommendPlans();
                if (recommendPlans == null || recommendPlans.isEmpty()) {
                    return "분석된 통신 성향에 맞는 요금제를 찾지 못했습니다. 다시 시도해 주세요.";
                }

                String recommendationsText = recommendPlans.stream()
                        .map(recommend -> {
                            PlanDto plan = recommend.getPlan();
                            return String.format(
                                    "요금제: '%s'\n- 월정액: %s원\n- 제공량: %s %s (통화량: %s분)\n",
                                    plan.getPlanName(),
                                    plan.getMonthlyFee(),
                                    plan.getDataAllowance(),
                                    plan.getDataAllowanceUnit(),
                                    plan.getAdditionalCallAllowance()
                            );
                        })
                        .collect(Collectors.joining("\n"));

                String finalReply = String.format(
                        "고객님의 통신 성향을 바탕으로 다음 요금제들을 추천해 드립니다.\n\n%s\n또 저랑 무엇을 하길 원하나요? 요금제 추천, 사용자 정보 알기, 심심풀이 중 고르세요",
                        recommendationsText
                );

                saveChatMessage(userId, finalReply, true);
                return finalReply;
            } catch (Exception e) {
                return "통신성향 분석 또는 요금제 추천 중 오류가 발생했습니다!!!! 다시 시도해 주세요. 또 저랑 무엇을 하길 원하나요? 요금제 추천, 사용자 정보 알기, 심심풀이 중 고르세요";
            }
        }

        return response;
    }
    private void saveChatMessage(Long userId, String message, boolean isBot) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUserId(userId);
        chatMessage.setMessage(message);
        chatMessage.setBot(isBot);

        // LocalDateTime -> 유닉스 타임스탬프 (초 단위)
        long unixTimestamp = LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .toEpochSecond();

        chatMessage.setTimestamp(unixTimestamp);

        chatMessageRepository.save(chatMessage);
    }
    private void saveForbiddenWordRecord(Long userId,String message){
        Long chatMessageId = chatMessageRepository.findTopByOrderByIdDesc().getId();
        badWordService.sendBadwordRecord(userId, chatMessageId, message);
    }
    private RecommendationResponseDto sendToRecommendationModule(UserPreferenceDto preference, Long userId) {
        BaseResponseDto<RecommendationResponseDto> recommendBaseResponse = recommendClient.recommend(preference, userId);
        log.info("recommendBaseResponse : {}", recommendBaseResponse);
        return recommendBaseResponse.getData();
    }

    private List<RecommendPlanDto> sendKeywordToRecommendationModule(String keyword) {
        BaseResponseDto<List<RecommendPlanDto>> recommendBaseResponse = recommendClient.recommendByKeyword(keyword);
        log.info("recommendBaseResponse : {}", recommendBaseResponse);
        return recommendBaseResponse.getData();
    }
}