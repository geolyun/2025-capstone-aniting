package com.example.aniting.controller;

import com.example.aniting.model.RecommendationRequest;
import com.example.aniting.model.RecommendationResult;
import com.example.aniting.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @PostMapping
    public RecommendationResult recommendPets(@RequestBody RecommendationRequest request) throws Exception {
        return null;
    }

}
