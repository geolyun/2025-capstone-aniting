package com.example.aniting.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendHistoryDTO {
    private Long historyId;
    private String userId;
    private Long top1PetId;
    private Long top2PetId;
    private Long top3PetId;
    private String aiReason;
    private LocalDateTime createdAt;
}
