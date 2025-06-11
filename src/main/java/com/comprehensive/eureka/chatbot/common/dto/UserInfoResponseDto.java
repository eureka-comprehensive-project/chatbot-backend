package com.comprehensive.eureka.chatbot.common.dto;

import com.comprehensive.eureka.chatbot.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponseDto {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private LocalDate birthday;
    private LocalDateTime createdAt;
    private Status status;
    private LocalDateTime unbanTime;
}