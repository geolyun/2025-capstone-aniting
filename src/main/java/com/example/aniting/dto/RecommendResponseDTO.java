package com.example.aniting.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendResponseDTO {
    private Long responseId;
    private String usersId;
    private Integer questionOrder;
    private String question;
    private String category;
    private String answer;
    private LocalDateTime createdAt;
}
