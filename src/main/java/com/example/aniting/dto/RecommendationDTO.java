package com.example.aniting.dto;

import lombok.Data;

@Data
public class RecommendationDTO {
    private int rank;
    private String animal;
    private String species;
    private String breed;
    private String careLevel;
    private String traitScores;
    private String isSpecial;
    private String reason;
}
