package com.comprehensive.eureka.chatbot.badword.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    private static final String KEY = "forbidden:words";
    public void saveForbiddenWord(String word) {
        redisTemplate.opsForSet().add(KEY, word);
    }

    public boolean isBadWord(String word) {
        String value = redisTemplate.opsForValue().get("badword:" + word);
        return "true".equals(value);
    }
    public Set<String> getAllBadWords() {
        // 1. badword: 로 시작하는 모든 key 검색
        Set<String> keys = redisTemplate.keys("badword:*");

        // 2. key 이름에서 'badword:' prefix 제거
        if (keys == null) return Set.of();

        return keys.stream()
                .map(key -> key.replace("badword:", ""))
                .collect(Collectors.toSet());
    }
    public void deleteBadWord(String word) {
        redisTemplate.delete("badword:" + word);
    }
}
