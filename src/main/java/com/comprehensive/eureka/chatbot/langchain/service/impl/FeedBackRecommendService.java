//package com.comprehensive.eureka.chatbot.langchain.service.impl;
//
//import com.comprehensive.eureka.chatbot.chatroom.entity.ChatRoom;
//import com.comprehensive.eureka.chatbot.client.RecommendClient;
//import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
//import com.comprehensive.eureka.chatbot.langchain.dto.ChatResponseDto;
//import com.comprehensive.eureka.chatbot.langchain.dto.FeedBackDto;
//import com.comprehensive.eureka.chatbot.langchain.dto.RecommendPlanDto;
//import com.comprehensive.eureka.chatbot.langchain.dto.RecommendationResponseDto;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@RequiredArgsConstructor
//public class FeedBackRecommendService {
//    private final RecommendClient recommendClient;
//    private final ChatResponseDto chatResponseDto;
//    public ChatResponseDto getFeedBackRecommendService(String response, List<RecommendPlanDto> recommendPlans,RecommendationResponseDto recommendationResponseDto, Map<Long,Boolean> promptProcessing, Long userId, , Long chatRoomId,ChatRoom currentChatRoom){
//        log.info("feedback 진입");
//        Long feedBackCode =  JsonFeedbackParser.parseFeedbackResponse(response).getFeedbackCode();
//        FeedBackDto feedBackDto = FeedBackDto.builder()
//                .sentimentCode(2L)
//                .detailCode(feedBackCode)
//                .build();
//
//        log.info("feedback 진입 후 json parse 성공, feedbackcode:"+feedBackCode);
//        RecommendationResponseDto recommendationResponseDto2= null;
//        if(recommendPlans == null){
//            recommendationResponseDto2= this.sendFeedBackToRecommendationModule(feedBackDto,userId,recommendationResponseDto.getRecommendPlans().get(0).getPlan().getPlanId());
//        }else{
//            recommendationResponseDto2= this.sendFeedBackToRecommendationModule(feedBackDto,userId,recommendPlans.get(0).getPlan().getPlanId());
//        }
//
//        log.info("feedback 진입 후 json parse 성공 후 recommend 호출 성공");
//        promptProcessing.put(userId, false);
//        chatResponseDto = generatePlanRecommendReply(recommendationResponseDto2,userId,currentChatRoom,chatRoomId);
//        return chatResponseDto;
//    }
//
//    private RecommendationResponseDto sendFeedBackToRecommendationModule(FeedBackDto feedBackDto, Long userId, Long planId) {
//        try {
//            BaseResponseDto<RecommendationResponseDto> recommendBaseResponse = recommendClient.recommendAfterFeedback(feedBackDto, userId, planId);
//            log.info("recommendBaseResponse : {}", recommendBaseResponse);
//            return recommendBaseResponse.getData();
//        } catch (Exception e) {
//            log.error("sendFeedBackToRecommendationModule 예외 발생", e);
//            throw e; // 예외를 다시 던지거나 null 반환 등 처리
//        }
//    }
//
//
//
//}
