package com.comprehensive.eureka.chatbot.client.dto.response;


import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class GetUserProfileDetailResponseDto {
    private String email;
    private String name;
    private String phone;
    private LocalDate birthday;
    private LocalDateTime createdAt;

}