package com.comprehensive.eureka.chatbot.badword.redis.service;

import java.util.Set;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AllowWordRedisService {

    private static final String KEY = "allow:words";

    private final StringRedisTemplate redisTemplate;

    public AllowWordRedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Set<String> getAllAllowWords() {
        return redisTemplate.opsForSet().members(KEY);
    }
}
