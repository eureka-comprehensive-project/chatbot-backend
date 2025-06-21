package com.comprehensive.eureka.chatbot.langchain.service.util;

import com.comprehensive.eureka.chatbot.client.PlanClient;
import com.comprehensive.eureka.chatbot.client.dto.response.FilterListResponseDto;
import com.comprehensive.eureka.chatbot.common.exception.ChatBotException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.comprehensive.eureka.chatbot.common.exception.ErrorCode.CHATBOT_PROMPT_ERROR;

@Component
@RequiredArgsConstructor
public class ParsingPlansUtil {
    private final PlanClient planClient;
    public static Long getPlanName(String parseTarget,String response,PlanClient planClient){
        List<String> planNames = new ArrayList<>();
        Matcher matcher = Pattern.compile("planName=([^,]+)").matcher(parseTarget);
        while (matcher.find()) {
            planNames.add(matcher.group(1).trim());
        }
        Long planId = planClient.getAllPlans().stream()
                .filter(p -> planNames.get(Integer.parseInt(response)).equals(p.getPlanName()))
                .map(FilterListResponseDto::getPlanId)
                .map(Long::valueOf)
                .findFirst()
                .orElseThrow(() -> new ChatBotException(CHATBOT_PROMPT_ERROR));
        return planId ;
    }
}
