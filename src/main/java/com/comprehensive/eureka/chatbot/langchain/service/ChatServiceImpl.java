package com.comprehensive.eureka.chatbot.langchain.service;

import com.comprehensive.eureka.chatbot.badword.service.BadwordServiceImpl;
import com.comprehensive.eureka.chatbot.chatroom.entity.ChatRoom;
import com.comprehensive.eureka.chatbot.chatroom.repository.ChatRoomRepository;
import com.comprehensive.eureka.chatbot.client.RecommendClient;
import com.comprehensive.eureka.chatbot.client.SentimentClient;
import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import com.comprehensive.eureka.chatbot.langchain.dto.*;
import com.comprehensive.eureka.chatbot.langchain.dto.RecommendationResponseDto;
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
    private final ChatRoomRepository chatRoomRepository;
    private String systemPrompt;
    private String userInfoPrompt;
    private String funnyChatPrompt;
    private String whatTodoPrompt;
    private String jsonExtractionPrompt;

    @PostConstruct
    public void loadPrompts() {
        try {
            Resource systemResource = new ClassPathResource("prompts/system-prompt.txt");
            try (InputStream in = systemResource.getInputStream()) {
                this.systemPrompt = new String(in.readAllBytes(), StandardCharsets.UTF_8);
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
    public String generateReply(Long userId, Long chatRoomId, String message) {

        log.info("generateReply 메서드 호출됨. 사용자 ID: {}, 채팅방 ID: {}, 메시지: {}", userId, chatRoomId, message);

        // ChatRoom 조회 부분
        ChatRoom currentChatRoom;
        try {
            currentChatRoom = chatRoomRepository.findById(chatRoomId)
                    .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다. ID: " + chatRoomId));
            log.info("채팅방 [ID: {}] 을 성공적으로 찾았습니다.", currentChatRoom.getChatRoomId());
        } catch (IllegalArgumentException e) {
            log.error("채팅방 조회 중 오류 발생: {}. 요청된 채팅방 ID: {}", e.getMessage(), chatRoomId, e);
            throw e;
        }

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
        saveChatMessage(userId, currentChatRoom, message, false);

        //금칙어 포함 시 금칙어 사용 기록에 저장 ( admin 모듈 ) 후 처리
        try {
            if (badWordService.checkBadWord(message)) {
                ChatMessage lastSavedMessage = chatMessageRepository.findTopByOrderByIdDesc();
                if(lastSavedMessage != null ){
                    badWordService.sendBadwordRecord(userId, lastSavedMessage.getId(), message);
                } else {
                    log.warn("No message found to link with bad word for userId : {}", userId);
                }
                saveForbiddenWordRecord(userId,message);
                return "부적절한 표현이 감지되어 답변할 수 없습니다." + message;
            }
        } catch (Exception e) {
            return "지금 현재 admin 모듈의 금칙어와 chatbot 모듈의 금칙어가 동기화돼있지 않아, 기록을 남길 수 없습니다. admin 모듈에서 해당 단어를 추가한 후에 다시 시도하세요";
        }
        
        // TODO

        // GPT 응답
        String response = chain.execute(message);
        saveChatMessage(userId, currentChatRoom, response, true);

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
            memory.add(SystemMessage.from(systemPrompt + attitude));
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
                    root = objectMapper.readTree(rawJson);

                    // UserPreferenceDto 필드 유효성 검사
                    if (root.hasNonNull("userId") && root.get("userId").canConvertToLong()
                            && root.hasNonNull("preferenceDataUsage") && root.get("preferenceDataUsage").isInt()
                            && root.hasNonNull("preferenceDataUsageUnit") && root.get("preferenceDataUsageUnit").isTextual()
                            && root.hasNonNull("preferenceSharedDataUsage") && root.get("preferenceSharedDataUsage").isInt()
                            && root.hasNonNull("preferenceSharedDataUsageUnit") && root.get("preferenceSharedDataUsageUnit").isTextual()
                            && root.hasNonNull("preferencePrice") && root.get("preferencePrice").isInt()
                            && root.hasNonNull("preferenceBenefitGroupId") && root.get("preferenceBenefitGroupId").isInt()
                            && root.hasNonNull("isPreferenceFamilyData") && root.get("isPreferenceFamilyData").isBoolean()
                            && root.hasNonNull("preferenceValueAddedCallUsage") && root.get("preferenceValueAddedCallUsage").isInt()) {
                        valid = true;
                        break;
                    }
                    attempt++;
                }

                if (!valid) {
                    return "통신성향 분석 중 오류가 발생했습니다. 다시 시도해 주세요.";
                }

                UserPreferenceDto preference =objectMapper.treeToValue(root, UserPreferenceDto.class);

                RecommendationResponseDto responsePlan =
                        sendToRecommendationModule(preference);
                PlanDto plan = responsePlan.getRecommendPlans().get(0).getPlan();
                String finalReply = String.format(
                        "고객님께 추천드리는 요금제는 '%s'입니다. 월 %s원이며, %s 등이 포함되어 있습니다." + " 또 저랑 무엇을 하길 원하나요? 요금제 추천, 사용자 정보 알기, 심심풀이 중 고르세요",
                        plan.getPlanName(), plan.getMonthlyFee(), plan.getAdditionalCallAllowance()
                );

                saveChatMessage(userId, currentChatRoom, finalReply, true);
                return finalReply;
            } catch (Exception e) {
                return "통신성향 분석 또는 요금제 추천 중 오류가 발생했습니다. 다시 시도해 주세요. 또 저랑 무엇을 하길 원하나요? 요금제 추천, 사용자 정보 알기, 심심풀이 중 고르세요";
            }
        }

        return response;
    }

    private void saveChatMessage(Long userId, ChatRoom chatRoom, String message, boolean isBot) {
        log.debug("채팅 메시지 저장 준비: 사용자 ID={}, 채팅방 ID={}, 챗봇 여부={}, 메시지='{}'",
                userId, chatRoom.getChatRoomId(), isBot, message);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUserId(userId);
        chatMessage.setChatRoom(chatRoom);
        chatMessage.setMessage(message);
        chatMessage.setBot(isBot);

        // LocalDateTime -> 유닉스 타임스탬프 (초 단위)
        long unixTimestamp = LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .toEpochSecond();

        chatMessage.setTimestamp(unixTimestamp);

        chatMessageRepository.save(chatMessage);

        try {
            chatMessageRepository.save(chatMessage);
            log.info("채팅 메시지 성공적으로 저장됨. ID: {}", chatMessage.getId());
        } catch (Exception e) {
            log.error("채팅 메시지 DB 저장 실패! 사용자 ID: {}, 채팅방 ID: {}. 메시지: '{}'", userId, chatRoom.getChatRoomId(), message, e);
        }
    }
    private void saveForbiddenWordRecord(Long userId,String message){
        Long chatMessageId = chatMessageRepository.findTopByOrderByIdDesc().getId();
        badWordService.sendBadwordRecord(userId, chatMessageId, message);
    }
    private RecommendationResponseDto sendToRecommendationModule(UserPreferenceDto preference) {
        BaseResponseDto<RecommendationResponseDto> recommend = recommendClient.recommend(preference);
        log.info("recommend : {}", recommend);
        return recommend.getData();
    }

    @Transactional
    public List<ChatHistoryResponseDto> getChatHistory(ChatHistoryRequestDto request) {
        log.info("채팅 이력 조회 요청. 채팅방 ID: {}, 사용자 ID: {}, 마지막 메시지 ID: {}, 페이지 크기: {}",
                request.getChatRoomId(), request.getUserId(), request.getLastMessageId(), request.getPageSize());

        Pageable pageable = PageRequest.of(0, request.getPageSize());
        List<ChatMessage> chatMessages;

        try {
            if(request.getLastMessageId() != null) {
                log.info("이전 메시지부터 조회. 채팅방 ID: {}, 마지막 메시지 ID: {}", request.getChatRoomId(), request.getLastMessageId());
                chatMessages = chatMessageRepository.findPriorMessages(
                        request.getChatRoomId(),
                        request.getLastMessageId(),
                        pageable
                );
            } else {
                log.info("최근 메시지 조회. 채팅방 ID: {}", request.getChatRoomId());
                chatMessages = chatMessageRepository.findRecentMessages(
                        request.getChatRoomId(),
                        pageable
                );
            }
            log.info("조회된 메시지 수: {}", chatMessages.size());

            return chatMessages.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("채팅 이력 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("채팅 이력 조회 실패", e);
        }
    }

    private ChatHistoryResponseDto convertToDto(ChatMessage chatMessage) {
        log.debug("ChatMessage를 DTO로 변환 중: 메시지 ID={}, 내용='{}'", chatMessage.getId(), chatMessage.getMessage());
        LocalDateTime localDateTime;
        try {
            localDateTime = Instant.ofEpochSecond(chatMessage.getTimestamp())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            log.debug("타임스탬프 변환 성공: {} -> {}", chatMessage.getTimestamp(), localDateTime);
        } catch (Exception e) {
            log.error("타임스탬프 변환 중 오류 발생. 원본 타임스탬프: {}", chatMessage.getTimestamp(), e);
            throw new RuntimeException("타임스탬프 변환 오류", e);
        }

        return new ChatHistoryResponseDto(
                chatMessage.getId(),
                chatMessage.getMessage(),
                chatMessage.getUserId(),
                chatMessage.getChatRoom().getChatRoomId(),
                chatMessage.isBot(),
                localDateTime
        );
    }
}