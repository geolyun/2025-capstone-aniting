package com.example.aniting.service;

import com.example.aniting.dto.*;
import com.example.aniting.gpt.OpenAiClient;
import com.example.aniting.gpt.RecommendationPrompt;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecommendationService {

    private final OpenAiClient openAiClient;

    public RecommendationService(OpenAiClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    public RecommendationResultDTO getRecommendations(AnswerRequestDTO responses) {
        String prompt = RecommendationPrompt.buildPrompt(responses.getAnswers());
        String gptResponse = openAiClient.callGPTAPI(prompt);
        return RecommendationPrompt.parseGptResponse(gptResponse);
    }
}