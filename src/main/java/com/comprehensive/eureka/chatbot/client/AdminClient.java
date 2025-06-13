package com.comprehensive.eureka.chatbot.client;

import com.comprehensive.eureka.chatbot.badword.dto.request.UserForbiddenWordsChatCreateRequestDto;
import com.comprehensive.eureka.chatbot.util.WebClientUtil;
import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminClient {
    private final WebClientUtil webClientUtil;

    public BaseResponseDto<Object> insertForbiddenWordRecord(UserForbiddenWordsChatCreateRequestDto userForbiddenWordsChatCreateRequestDto) {
        return webClientUtil.post(
                "http://localhost:8086//admin/forbidden-words/chats",
                userForbiddenWordsChatCreateRequestDto,
                new ParameterizedTypeReference<>() {
                }
        );
    }

}