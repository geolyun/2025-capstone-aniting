package com.example.aniting.controller;

import com.example.aniting.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aniting.dto.*;

@RestController
@RequestMapping("/api")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController (RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/recommend")
    public ResponseEntity<RecommendationResultDTO> recommend(@RequestBody AnswerRequestDTO responses) {
        return ResponseEntity.ok(recommendationService.getRecommendations(responses));
    }
}

/*
    @PostMapping("/analyze")
    public ScoreResult analyzeResponses(@RequestBody AnswerRequest request) throws Exception {
        return recommendationService.analyzeUserResponses(request.getAnswers());
    }

    @PostMapping("/pets")
    public RecommendationResult recommendPets(@RequestBody AnswerRequest request) throws Exception {
        return recommendationService.getRecommendations(request.getAnswers());
    }
}
*/
