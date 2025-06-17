package com.comprehensive.eureka.chatbot.client;

import com.comprehensive.eureka.chatbot.badword.dto.request.UserForbiddenWordsChatCreateRequestDto;
import com.comprehensive.eureka.chatbot.common.dto.BaseResponseDto;
import com.comprehensive.eureka.chatbot.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserClient {
    private final WebClientUtil webClientUtil;

    public BaseResponseDto<Object> getUserProfile(UserForbiddenWordsChatCreateRequestDto userForbiddenWordsChatCreateRequestDto) {
        return webClientUtil.post(
//                "https://www.visiblego.com/user/profile",
                "http://localhost:8085/user/profile",
                userForbiddenWordsChatCreateRequestDto,
                new ParameterizedTypeReference<>() {
                }
        );
    }

}