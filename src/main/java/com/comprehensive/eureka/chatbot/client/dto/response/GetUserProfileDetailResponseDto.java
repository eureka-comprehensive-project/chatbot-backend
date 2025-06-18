package com.comprehensive.eureka.chatbot.client.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class GetUserProfileDetailResponseDto {
    private String email;
    private String name;
    private String phone;
    private LocalDate birthday;
    private LocalDateTime createdAt;

}