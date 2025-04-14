package com.example.aniting.controller;

import com.example.aniting.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.aniting.dto.*;

@RestController
@RequestMapping("/api")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController (RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/recommend")
    public ResponseEntity<RecommendationResultDTO> recommend(@RequestHeader("user-id") String userId, @RequestBody AnswerRequestDTO requestDTO) {
        return ResponseEntity.ok(recommendationService.getRecommendations(userId, requestDTO));
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
