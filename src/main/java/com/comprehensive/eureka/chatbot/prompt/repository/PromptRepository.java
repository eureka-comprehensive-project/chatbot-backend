package com.comprehensive.eureka.chatbot.prompt.repository;

import com.comprehensive.eureka.chatbot.prompt.entity.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromptRepository extends JpaRepository<Prompt, Long> {
    boolean existsBySentimentCode(int sentimentCode);

    boolean existsByName(String name);

    Prompt findByName(String name);
}
