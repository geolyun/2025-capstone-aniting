package com.example.aniting.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersDTO {
    private Long usersNo;
    private String usersId;
    private String usersNm;
    private String passwd;
    private String securityQuestion;
    private String securityAnswer;
    private LocalDateTime joinAt;
    private LocalDateTime updatedAt;
    private String activeYn;
    private LocalDateTime inactiveAt;
}

