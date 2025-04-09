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
    private String userId;
    private String aiPrompt;
    private String aiResponse;
    private LocalDateTime createdAt;
}
