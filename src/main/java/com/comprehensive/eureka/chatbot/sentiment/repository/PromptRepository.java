package com.comprehensive.eureka.chatbot.sentiment.repository;

import com.comprehensive.eureka.chatbot.sentiment.entity.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromptRepository extends JpaRepository<Prompt, Long> {
    boolean existsBySentimentCode(int sentimentCode);

    boolean existsByName(String name);

    Prompt findBySentimentCode(int sentimentCode);
    Optional<Prompt> findByName(String name);
}
