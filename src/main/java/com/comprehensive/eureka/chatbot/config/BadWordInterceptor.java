package com.comprehensive.eureka.chatbot.config;


import com.comprehensive.eureka.chatbot.badword.dto.request.BadwordRequest;
import com.comprehensive.eureka.chatbot.badword.dto.request.UserForbiddenWordsChatCreateRequestDto;
import com.comprehensive.eureka.chatbot.client.AdminClient;
import com.comprehensive.eureka.chatbot.common.dto.UserInfoResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vane.badwordfiltering.BadWordFiltering;
import io.micrometer.common.lang.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RequiredArgsConstructor
@Component
public class BadWordInterceptor implements HandlerInterceptor {

        private final BadWordFiltering badWordFilter;
//        @Qualifier("adminClient")
//        private final WebClient client;
////        @Qualifier("userClient")
////        private final WebClient userClient;
        private final AdminClient adminClient;
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                if (request instanceof ContentCachingRequestWrapper wrapper) {
                        String body = new String( wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                                Map<String, String> map = objectMapper.readValue(body, Map.class);
                                String message = map.get("message");

                                //1.get userId
//                                Long userId = Long.valueOf(map.get("userId"));

                                //2.get chatting id

                                //3. request구성
                                List<String> matchingWords = new ArrayList<>();
                                matchingWords.add("씨발");
                                matchingWords.add("씨발");

                                UserForbiddenWordsChatCreateRequestDto userForbiddenWordsChatCreateRequestDto = UserForbiddenWordsChatCreateRequestDto.builder()
                                        .userId(123L)
                                        .chatMessageId(456L)
                                        .forbiddenWords(Arrays.asList("씨발", "씨발"))
                                        .build();

                                if(badWordFilter.blankCheck(message)){
                                        //4.
                                        ObjectMapper mapper = new ObjectMapper();
                                        String json = mapper.writeValueAsString(userForbiddenWordsChatCreateRequestDto);
                                        System.out.println("Request JSON = " + json);
                                        adminClient.insertForbiddenWordRecord(userForbiddenWordsChatCreateRequestDto);
                                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                        return false;
                                }
                        } catch (Exception e) {
                                System.out.println("비속어 감지 처리중 에러: " + e.getMessage());
                        }
                }
                return true;
        }
        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                               @Nullable ModelAndView modelAndView) throws Exception {
                System.out.println("isCommitted: " + response.isCommitted());
                System.out.println("MyInterceptor2 >>> posthandle " + request.getRequestURI());

        }
}
