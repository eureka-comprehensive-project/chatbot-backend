package com.comprehensive.eureka.chatbot.badword.dto;

import com.vane.badwordfiltering.BadWordFiltering;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
@Data
@RequiredArgsConstructor
public class BadWord {
    private final BadWordFiltering badwordFiltering;

}
