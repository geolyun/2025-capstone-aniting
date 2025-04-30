package com.example.aniting.recommendation;

import com.example.aniting.dto.AnswerRequestDTO;
import com.example.aniting.dto.RecommendationResultDTO;
import com.example.aniting.dto.UsersDTO;
import com.example.aniting.recommendation.RecommendationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    // 🔵 질문 10개 요청
    @GetMapping("/questions")
    public ResponseEntity<List<String>> generateQuestions() {
        List<String> questions = recommendationService.generateQuestions();
        return ResponseEntity.ok(questions);
    }

    // 🔵 추천 요청
    @PostMapping("/submit")
    public ResponseEntity<RecommendationResultDTO> submitAnswers(
            @RequestHeader("user-id") String userId,
            @RequestBody AnswerRequestDTO answerRequestDTO
    ) {
        RecommendationResultDTO result = recommendationService.getRecommendations(userId, answerRequestDTO);
        return ResponseEntity.ok(result);
    }
}