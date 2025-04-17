package com.example.aniting.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreDTO {
    private Long scoreId;
    private String usersId;
    private Long categoryId;
    private Integer scoreValue;
}
