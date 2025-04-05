package com.example.aniting.model;

import java.util.Map;

public class RecommendationRequest {
    private Map<String, String> responses;

    public Map<String, String> getResponses() {
        return responses;
    }

    public void setResponses(Map<String, String> responses) {
        this.responses = responses;
    }

}
