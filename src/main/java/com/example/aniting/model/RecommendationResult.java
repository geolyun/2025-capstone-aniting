package com.example.aniting.model;

import java.util.List;
import java.util.Map;

public class RecommendationResult {
    private Map<String, Integer> scores;
    private List<String> recommendations;

    public RecommendationResult(Map<String, Integer> scores, List<String> recommendations) {
        this.scores = scores;
        this.recommendations = recommendations;
    }

    public Map<String, Integer> getScores() {
        return scores;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }
}
