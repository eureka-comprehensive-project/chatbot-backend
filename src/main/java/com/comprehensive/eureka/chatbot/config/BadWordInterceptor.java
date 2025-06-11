package com.comprehensive.eureka.chatbot.config;

import com.comprehensive.eureka.chatbot.badword.dto.request.BadwordRecordRequest;
import com.comprehensive.eureka.chatbot.badword.dto.request.BadwordRequest;
import com.comprehensive.eureka.chatbot.common.dto.UserInfoResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vane.badwordfiltering.BadWordFiltering;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RequiredArgsConstructor
@Component
public class BadWordInterceptor implements HandlerInterceptor {

        private final BadWordFiltering badWordFilter;
        @Qualifier("adminClient")
        private final WebClient adminClient;
        @Qualifier("userClient")
        private final WebClient userClient;

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                if (request instanceof ContentCachingRequestWrapper wrapper) {
                        String body = new String( wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                                Map<String, String> map = objectMapper.readValue(body, Map.class);
                                String message = map.get("message");
                                //get userId
                                Long userId = 0L;
                                userClient.get()
                                        .uri("/user/")
                                        .retrieve()
                                        .bodyToMono(UserInfoResponseDto.class);

                                BadwordRecordRequest badwordRecordRequest = BadwordRecordRequest.builder()
                                        .userId(userId)
                                        .message(message)
                                        .snippets(List.of("asdf", "asdf"))
                                        .build();
                                List<String> matchingWords = new ArrayList<>();
                                if(badWordFilter.stream().anyMatch(message::contains)){

                                }
                                if (badWordFilter.blankCheck(message)) {
                                        System.out.println("비속어 감지됨");
                                        adminClient.post()
                                                        .uri("/admin/forbidden-words/")
                                                        .bodyValue(badwordRecordRequest);
                                         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                         return false;
                                }

                        } catch (Exception e) {
                                System.out.println("JSON 파싱 실패: " + e.getMessage());
                        }
                }

                return true;
        }



}
