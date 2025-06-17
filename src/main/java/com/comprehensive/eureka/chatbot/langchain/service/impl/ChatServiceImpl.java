package com.comprehensive.eureka.chatbot.langchain.service.impl;

import com.comprehensive.eureka.chatbot.badword.service.BadwordServiceImpl;
import com.comprehensive.eureka.chatbot.chatroom.entity.ChatRoom;
import com.comprehensive.eureka.chatbot.chatroom.repository.ChatRoomRepository;
import com.comprehensive.eureka.chatbot.client.PlanClient;
import com.comprehensive.eureka.chatbot.client.RecommendClient;
import com.comprehensive.eureka.chatbot.client.SentimentClient;
import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import com.comprehensive.eureka.chatbot.langchain.dto.*;
import com.comprehensive.eureka.chatbot.langchain.entity.ChatMessage;
import com.comprehensive.eureka.chatbot.langchain.repository.ChatMessageRepository;
import com.comprehensive.eureka.chatbot.langchain.service.ChatService;
import com.comprehensive.eureka.chatbot.prompt.service.PromptServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final OpenAiChatModel baseOpenAiModel;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMemoryStore memoryStore;
    private final TokenCountEstimator tokenCountEstimator;
    private final ObjectMapper objectMapper;
    private final RecommendClient recommendClient;
    private final SentimentClient sentimentClient;
    private final ChatRoomRepository chatRoomRepository;
    private String systemPrompt;
    private final PlanClient planClient;
    private String recommendPrompt;
    private String userInfoPrompt;
    private String funnyChatPrompt;
    private String whatTodoPrompt;
    private String jsonExtractionPrompt;
    private String keywordExtractionPrompt;
    private final BadwordServiceImpl badWordService;
    private final PromptServiceImpl promptService;

    // 사용자별 메모리 관리
    private final Map<Long, ChatMemory> userMemoryMap = new ConcurrentHashMap<>();
    private final Map<Long, Boolean> promptProcessing = new ConcurrentHashMap<>();
    private final Map<Long, Boolean> firstChatActivated = new ConcurrentHashMap<>();

    // 프롬프트 전환을위한 맵으로 구성 : 차후 확장된 프롬프트 하기 맵에 추가 필요
    Map<String, String> promptMap;

    Map<String, String> endSignalMap;

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

            this.promptMap = Map.of(
                    "[prompt전환]1번으로 예상", userInfoPrompt,
                    "[prompt전환]2번으로 예상", funnyChatPrompt,
                    "[prompt전환]3번으로 예상", recommendPrompt
            );

            this.endSignalMap = Map.of(
                    "사용자 정보 제공 준비 끝", "사용자 정보 제공",
                    "재밌는 이야기 였습니다", "심심풀이"
            );
        } catch (IOException e) {
            throw new UncheckedIOException("프롬프트 로드 중 오류 발생", e);
        }
    }


    @Override
    public ChatResponseDto generateReply(Long userId, Long chatRoomId, String message) throws JsonProcessingException {
        // 전역적으로 사용할 리턴 변수 생성
        ChatResponseDto chatResponseDto = ChatResponseDto.of("", chatRoomId, userId);
        // 요청값 출력
        log.info("generateReply 메서드 호출됨. 사용자 ID: {}, 채팅방 ID: {}, 메시지: {}", userId, chatRoomId, message);
        // ChatRoom 조회 부분
        ChatRoom currentChatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new IllegalArgumentException("채팅방을 찾을 수 없습니다. ID: " + chatRoomId)
        );
        // 감정 분석
        log.info("감정 분석 시작");
        String sentimentJson = sentimentClient.determineSentiment(new DetermineSentimentDto(message));
        String sentiment = objectMapper.readTree(sentimentJson).get("sentiment").asText();
        log.info("감정 분석 결과: " + sentiment);
        // 채팅방 마다 다른 memory 할당
        ChatMemory memory = userMemoryMap.computeIfAbsent(chatRoomId, id -> {
            TokenWindowChatMemory newMemory = TokenWindowChatMemory.builder()
                    .id(id)
                    .maxTokens(10000, tokenCountEstimator)
                    .chatMemoryStore(memoryStore)
                    .build();
            return newMemory;
        });
        //
        promptProcessing.putIfAbsent(chatRoomId, false);
        firstChatActivated.putIfAbsent(chatRoomId, true);
        // 만약 한 prompt 로직이 종료 됐다면, prompt 갈아 끼는 모드로 변경
        if (!promptProcessing.get(chatRoomId) || firstChatActivated.get(chatRoomId)) {
            log.info("prompt 전환 시작");
            firstChatActivated.put(chatRoomId, false);
            memory.add(SystemMessage.from(whatTodoPrompt));
            log.info("prompt 전환 끝");
        }

        // 설정한 prompt 저장된 memory 대로 lang chain 생성
        ConversationalChain chain = ConversationalChain.builder()
                .chatModel(baseOpenAiModel)
                .chatMemory(memory)
                .build();

        // 사용자 메시지 저장
        Long chatMessageId = saveChatMessage(userId, currentChatRoom, message, false, false, "mockReaseon");
        // TODO 잠시 주석처리 키워드 필터링 작업
//        boolean checkResult = badWordCheck(userId, message);
//        if (checkResult) {
//            return ChatResponseDto.fail("잘못된 키워드 입니다.", chatResponseDto);
//        }

        // GPT 응답
        String response = chain.execute(message);
        saveChatMessage(userId, currentChatRoom, response, true, false, "mockReason");

        // 감정 기반 태도
        String attitude = promptService.getPromptBySentimentName(sentiment).getScenario();

        // Prompt 전환 탐지 및 적용
        Optional<String> switchKey = detectPromptSwitch(response);
        if (switchKey.isPresent()) {
            String prompt = promptMap.get(switchKey.get()) + attitude;
            log.info("{} 감지됨", switchKey.get());
            memory.add(SystemMessage.from(prompt));
            promptProcessing.put(chatRoomId, true);
            response = chain.execute(message);
        } else if (response.contains("[prompt전환]4번으로 예상")) {
            promptProcessing.put(chatRoomId, false);
            return ChatResponseDto.fail("못 알아들었습니다. 저랑 무엇을 하길 원하나요? 요금제 추천, 사용자 정보 알기, 심심풀이 중 고르세요", chatResponseDto);
        }

        // Prompt 종료 탐지
        Optional<String> endSignalKey = detectEndSignal(response);
        if (endSignalKey.isPresent()) {
            String context = endSignalMap.get(endSignalKey.get());
            return endPromptAndRespond(context, chatRoomId, userId);
        }

        if (response.contains("직업을 확인하였습니다") || response.contains("키워드를 확인하였습니다")) {
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
                return ChatResponseDto.of("키워드 추출 중 오류가 발생했습니다. 다시 시도해 주세요.", chatRoomId, userId);
            }


            log.info("extractedKeyword : {}", extractedKeyword);
            List<RecommendPlanDto> recommendPlans = sendKeywordToRecommendationModule(extractedKeyword);
            if (recommendPlans == null || recommendPlans.isEmpty()) {
                return ChatResponseDto.of("추천드릴 요금제를 찾지 못했습니다. 다른 키워드로 다시 시도해 주세요.", chatRoomId, userId);
            }

            String finalReply = "고객님께 다음 요금제들을 추천해 드립니다.\n\n" +
                    recommendPlans.stream()
                            .map(recommend -> {
                                PlanDto plan = recommend.getPlan();
                                return String.format(
                                        "요금제: '%s'\n" +
                                                "- 월정액: %d원\n" +
                                                "- 제공량: %d %s\n" +
                                                "- 제공 기간: %s\n" +
                                                "- 테더링 데이터: %d %s\n" +
                                                "- 음성 통화량: %d분\n" +
                                                "- 추가 통화 허용량: %d분\n" +
                                                "- 가족 결합 가능: %s\n" +
                                                "- 카테고리: %s\n",
                                        plan.getPlanName(),
                                        plan.getMonthlyFee(),
                                        plan.getDataAllowance(),
                                        plan.getDataAllowanceUnit(),
                                        plan.getDataPeriod(),
                                        plan.getTetheringDataAmount(),
                                        plan.getTetheringDataUnit(),
                                        plan.getVoiceCallAmount(),
                                        plan.getAdditionalCallAllowance(),
                                        plan.isFamilyDataEnabled() ? "가능" : "불가능",
                                        plan.getPlanCategory()
                                );
                            })
                            .collect(Collectors.joining("\n"));

            finalReply += " \n\n 또 저랑 무엇을 하길 원하나요? 요금제 추천, 사용자 정보 알기, 심심풀이 중 고르세요";
            saveChatMessage(userId, currentChatRoom, finalReply, true, true, "mockReason");
            promptProcessing.put(userId, false); //이 prompt 를 종료시키고 다시 promt 변경하게끔.
            ChatResponseDto chatResponseDto1 = ChatResponseDto.builder()
                    .messageId(chatMessageRepository.findTopByOrderByIdDesc().getId())
                    .userId(userId)
                    .chatRoomId(chatRoomId)
                    .message(response)
                    .isBot(true)
                    .isRecommended(true)
                    .recommendationReason("mockReason")
                    .build();

            return chatResponseDto1;
        }
        // 통신성향 수집 완료 신호 감지
        if (response.contains("통신성향을 모두 파악했습니다")) {
            promptProcessing.put(userId, false);
            JsonNode root = null;
            String rawJson;
            final int MAX_RETRIES = 2;
            int attempt = 0;
            boolean valid = false;

            while (attempt < MAX_RETRIES) {
                rawJson = chain.execute(jsonExtractionPrompt);

                log.info("rawJson 브라켓 추출하기 전: {}", rawJson);
                // START: 대괄호 안 혜택 추출 로직 추가
                int bracketStart = rawJson.indexOf('[');
                int bracketEnd = rawJson.indexOf(']');
                String premiumBenefit = null;
                String mediaBenefit = null;
                if (bracketStart >= 0 && bracketEnd > bracketStart) {
                    String bracketContent = rawJson.substring(bracketStart + 1, bracketEnd);
                    String[] parts = bracketContent.split("\\|");
                    if (parts.length > 0) premiumBenefit = parts[0].trim();
                    if (parts.length > 1) mediaBenefit = parts[1].trim();
                }
                BenefitRequestDto benefitDto = new BenefitRequestDto();
                benefitDto.setPremium(premiumBenefit);
                benefitDto.setMedia(mediaBenefit);
                log.info("premiumBenefit : {}", premiumBenefit);
                log.info("mediaBenefit : {}", mediaBenefit);

                // PlanClient로 혜택 그룹 ID 조회
                Long benefitGroupId = planClient.getBenefitIds(benefitDto);
                log.info("BenefitGroupId: {}", benefitGroupId);


                int start = rawJson.indexOf('{');
                int end = rawJson.lastIndexOf('}');
                if (start >= 0 && end > start) {
                    rawJson = rawJson.substring(start, end + 1);
                } else {
                    log.warn("JSON 추출 실패: 유효한 JSON 객체 형식이 아닙니다.");
                    attempt++;
                    continue;
                }
                log.info("rawJson : {}", rawJson);
                root = objectMapper.readTree(rawJson);
                // UserPreferenceDto 필드 유효성 검사

                if (root instanceof ObjectNode) {
                    ((ObjectNode) root).put("preferenceBenefitGroupId", benefitGroupId.intValue());
                }
                log.info("root : {}", root);

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

            log.info("final root : {}", root);

            if (!valid) {
                promptProcessing.put(userId, false); //이 prompt 를 종료시키고 다시 promt 변경하게끔.
                return ChatResponseDto.of("통신성향 분석 또는 요금제 추천 중 오류가 발생했습니다. 다시 시도해 주세요. 또 저랑 무엇을 하길 원하나요? 요금제 추천, 사용자 정보 알기, 심심풀이 중 고르세요", chatRoomId, userId);
            }

            UserPreferenceDto preference = objectMapper.treeToValue(root, UserPreferenceDto.class);

            log.info("preference : {}", preference);
            RecommendationResponseDto recommendationResponse = sendToRecommendationModule(preference, userId);
            log.info("recommendationResponse : {}", recommendationResponse);
            List<RecommendPlanDto> recommendPlans = recommendationResponse.getRecommendPlans();
            if (recommendPlans == null || recommendPlans.isEmpty()) {
                promptProcessing.put(userId, false); //이 prompt 를 종료시키고 다시 promt 변경하게끔.
                return ChatResponseDto.of("분석된 통신 성향에 맞는 요금제를 찾지 못했습니다. 다시 시도해 주세요.", chatRoomId, userId);
            }

            String recommendationsText = recommendPlans.stream()
                    .map(recommend -> {
                        PlanDto plan = recommend.getPlan();
                        log.info("plan : {}", plan);
                        return String.format(
                                "요금제: '%s'\n" +
                                        "- 월정액: %d원\n" +
                                        "- 제공량: %d %s\n" +
                                        "- 제공 기간: %s\n" +
                                        "- 테더링 데이터: %d %s\n" +
                                        "- 음성 통화량: %d분\n" +
                                        "- 추가 통화 허용량: %d분\n" +
                                        "- 가족 결합 가능: %s\n" +
                                        "- 카테고리: %s\n",
                                plan.getPlanName(),
                                plan.getMonthlyFee(),
                                plan.getDataAllowance(),
                                plan.getDataAllowanceUnit(),
                                plan.getDataPeriod(),
                                plan.getTetheringDataAmount(),
                                plan.getTetheringDataUnit(),
                                plan.getVoiceCallAmount(),
                                plan.getAdditionalCallAllowance(),
                                plan.isFamilyDataEnabled() ? "가능" : "불가능",
                                plan.getPlanCategory()
                        );
                    })
                    .collect(Collectors.joining("\n"));

            String finalReply = String.format(
                    "고객님의 통신 성향을 바탕으로 다음 요금제들을 추천해 드립니다.\n\n%s\n 또 저랑 무엇을 하길 원하나요? 요금제 추천, 사용자 정보 알기, 심심풀이 중 고르세요",
                    recommendationsText
            );

            saveChatMessage(userId, currentChatRoom, finalReply, true, true, "mock reason");
            promptProcessing.put(userId, false);
            ChatResponseDto.of(finalReply, chatRoomId, userId);

        }

        return ChatResponseDto.of(response, chatRoomId, userId);
    }

    private boolean badWordCheck(Long userId, String message) {
        //금칙어 포함 시 금칙어 사용 기록에 저장 ( admin 모듈 ) 후 처리
        boolean check = badWordService.checkBadWord(message);
        if (check) {
            saveForbiddenWordRecord(userId, message);
            return true;
        }
        return false;
    }

    private Long saveChatMessage(Long userId, ChatRoom chatRoom, String message, boolean isBot, boolean isRecomend, String recommendReason) {
        log.debug("채팅 메시지 저장 준비: 사용자 ID={}, 채팅방 ID={}, 챗봇 여부={}, 메시지='{}'", userId, chatRoom.getChatRoomId(), isBot, message);

        // LocalDateTime -> 유닉스 타임스탬프 (초 단위)
        long unixTimestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();

        ChatMessage chatMessage = ChatMessage.builder()
                .userId(userId)
                .chatRoom(chatRoom)
                .message(message)
                .isBot(isBot)
                .build();

        chatMessage.setTimestamp(unixTimestamp);

        ChatMessage chat = chatMessageRepository.save(chatMessage);
        return chat.getId();
    }

    private void saveForbiddenWordRecord(Long userId, String message) {
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

    public List<ChatHistoryResponseDto> getChatHistory(ChatHistoryRequestDto request) {
        log.info(
                "채팅 이력 조회 요청. 채팅방 ID: {}, 사용자 ID: {}, 마지막 메시지 ID: {}, 페이지 크기: {}",
                request.getChatRoomId(), request.getUserId(), request.getLastMessageId(), request.getPageSize()
        );
        Pageable pageable = PageRequest.of(0, request.getPageSize());
        List<ChatMessage> chatMessages;

        if (request.getLastMessageId() != null) {
            log.info("이전 메시지부터 조회. 채팅방 ID: {}, 마지막 메시지 ID: {}", request.getChatRoomId(), request.getLastMessageId());
            chatMessages = chatMessageRepository.findPriorMessages(
                    request.getChatRoomId(),
                    request.getUserId(),
                    request.getLastMessageId(),
                    pageable
            );
        } else {
            log.info("최근 메시지 조회. 채팅방 ID: {}", request.getChatRoomId());
            chatMessages = chatMessageRepository.findRecentMessages(
                    request.getChatRoomId(),
                    request.getUserId(),
                    pageable
            );
        }
        log.info("조회된 메시지 수: {}", chatMessages.size());
        return chatMessages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ChatHistoryResponseDto convertToDto(ChatMessage chatMessage) {
        log.debug("ChatMessage를 DTO로 변환 중: 메시지 ID={}, 내용='{}'", chatMessage.getId(), chatMessage.getMessage());

        LocalDateTime localDateTime = Instant.ofEpochSecond(chatMessage.getTimestamp())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        log.debug("타임스탬프 변환 성공: {} -> {}", chatMessage.getTimestamp(), localDateTime);

        return new ChatHistoryResponseDto(
                chatMessage.getId(),
                chatMessage.getMessage(),
                chatMessage.getUserId(),
                chatMessage.getChatRoom().getChatRoomId(),
                chatMessage.isBot(),
                localDateTime
        );
    }

    private Optional<String> detectPromptSwitch(String response) {
        return promptMap.keySet().stream()
                .filter(response::contains)
                .findFirst();
    }

    private Optional<String> detectEndSignal(String response) {
        return endSignalMap.keySet().stream()
                .filter(response::contains)
                .findFirst();
    }

    private ChatResponseDto endPromptAndRespond(String context, Long chatRoomId, Long userId) {
        log.info("{} 끝", context);
        promptProcessing.put(userId, false);
        String message = "저랑 무엇을 하길 원하나요? 요금제 추천, 사용자 정보 알기, 심심풀이 중 고르세요";
        return ChatResponseDto.of(message, chatRoomId, userId);
    }
}