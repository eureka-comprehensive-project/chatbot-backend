package com.comprehensive.eureka.chatbot.config;

import com.comprehensive.eureka.chatbot.badword.dto.request.BadwordRecordRequest;
import com.comprehensive.eureka.chatbot.badword.dto.request.BadwordRequest;
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
                                //1.get userId
//                                Long userId = 0L;
//                                userClient.get()
//                                        .uri("visible.com/user/")
//                                        .retrieve()
//                                        .bodyToMono(UserInfoResponseDto.class);
//                                if(badWordFilter.stream().anyMatch(message::contains)){
//                                }

                                //2.get chatting id

                                //3. request구성
                                BadwordRecordRequest badwordRecordRequest = BadwordRecordRequest.builder()
                                        .userId(0L)
                                        .chatMessageId(0L)
                                        .forbiddenWords(List.of("나쁜말1", "나쁜말2"))
                                        .build();
                                List<String> matchingWords = new ArrayList<>();

                                //4.
                                adminClient.post()
                                        .uri("https://visiblego.com/admin/forbidden-words/chats")
                                        .bodyValue(badwordRecordRequest)
                                        .retrieve()
                                        .bodyToMono(Void.class)
                                        .subscribe(
                                                result -> System.out.println("요청 성공"),
                                                error -> System.err.println("요청 실패: " + error.getMessage())
                                        );
                                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                                        return false;
                        } catch (Exception e) {
                                System.out.println("JSON 파싱 실패: " + e.getMessage());
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
