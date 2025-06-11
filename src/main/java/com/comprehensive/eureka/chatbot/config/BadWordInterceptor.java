package com.comprehensive.eureka.chatbot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vane.badwordfiltering.BadWordFiltering;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RequiredArgsConstructor
@Component
public class BadWordInterceptor implements HandlerInterceptor {

        private final BadWordFiltering badWordFilter;
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                if (request instanceof ContentCachingRequestWrapper wrapper) {
                        String body = new String( wrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                                Map<String, String> map = objectMapper.readValue(body, Map.class);
                                String message = map.get("message");

                                if (badWordFilter.check(message)) {
                                        System.out.println("비속어 감지됨");
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
