package com.example.aniting.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userNo;
    private String userId;
    private String userNm;
    private String passwd;
    private String securityQuestion;
    private String securityAnswer;
    private LocalDateTime joinAt;
    private LocalDateTime updatedAt;
    private String activeYn;
    private LocalDateTime inactiveAt;
}

