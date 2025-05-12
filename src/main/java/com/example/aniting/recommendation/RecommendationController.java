package com.example.aniting.recommendation;

import com.example.aniting.dto.AnswerItemDTO;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    // 🔵 질문 10개 요청
    @GetMapping("/questions")
    public ResponseEntity<List<String>> generateQuestions(HttpSession session) {
        List<AnswerItemDTO> items = recommendationService.generateQuestionItems();
        session.setAttribute("questionItems", items);

        List<String> questions = items.stream()
                .map(AnswerItemDTO::getQuestion)
                .toList();

        return ResponseEntity.ok(questions);
    }




    // 🔵 추천 요청
    @PostMapping("/submit")
    public ResponseEntity<RecommendationResultDTO> submitAnswers(
            @RequestHeader("user-id") String userId,
            @RequestBody AnswerRequestDTO answerRequestDTO,
            HttpSession session
    ) {
        // 세션에서 원본 질문 목록 가져오기
        List<AnswerItemDTO> originalItems = (List<AnswerItemDTO>) session.getAttribute("questionItems");
        Map<String, String> questionToCategory = new HashMap<>();
        for (AnswerItemDTO item : originalItems) {
            questionToCategory.put(item.getQuestion(), item.getCategory());
        }

        // 사용자 응답에 category를 복원
        for (AnswerItemDTO item : answerRequestDTO.getAnswers()) {
            item.setCategory(questionToCategory.getOrDefault(item.getQuestion(), null));
        }

        RecommendationResultDTO result = recommendationService.getRecommendations(userId, answerRequestDTO);
        return ResponseEntity.ok(result);
    }

}