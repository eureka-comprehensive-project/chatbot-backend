package com.comprehensive.eureka.chatbot.langchain.service.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Component
@RequiredArgsConstructor
@Data
public class SessionManager {
    private final Map<Long, Boolean> promptProcessing = new ConcurrentHashMap<>();

}
