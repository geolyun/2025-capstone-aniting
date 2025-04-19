package com.example.aniting.service;

import com.example.aniting.gpt.OpenAiClient;
import com.example.aniting.gpt.RecommendationPrompt;
import org.springframework.stereotype.Service;


@Service
public class RecommendationService {

    private final OpenAiClient openAiClient;

        String prompt = RecommendationPrompt.buildPrompt(responses.getAnswers());
        String gptResponse = openAiClient.callGPTAPI(prompt);
    }
    }