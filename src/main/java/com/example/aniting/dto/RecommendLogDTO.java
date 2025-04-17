package com.example.aniting.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendLogDTO {
    private Long logId;
    private String usersId;
    private String aiPrompt;
    private String aiResponse;
    private LocalDateTime createdAt;
}
