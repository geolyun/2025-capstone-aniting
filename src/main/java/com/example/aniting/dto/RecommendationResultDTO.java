package com.example.aniting.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RecommendationResultDTO {
    private Map<String, Integer> userScores;
    private List<RecommendationDTO> recommendations;
}

