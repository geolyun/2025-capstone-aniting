package com.example.aniting.model;

import java.util.Map;

public class RecommendationRequest {
    private Map<String, String> response;

    public Map<String, String> getResponses() {
        return response;
    }

    public void setResponses(Map<String, String> response) {
        this.response = response;
    }

}
