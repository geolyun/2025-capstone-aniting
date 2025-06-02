package com.example.aniting.recommendation;

import com.example.aniting.dto.AnswerItemDTO;
import com.example.aniting.dto.AnswerRequestDTO;
import com.example.aniting.dto.RecommendationResultDTO;
import com.example.aniting.entity.RecommendResponse;
import com.example.aniting.entity.Score;
import com.example.aniting.entity.Category;
import com.example.aniting.repository.CategoryRepository;
import com.example.aniting.repository.RecommendResponseRepository;
import com.example.aniting.repository.ScoreRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final RecommendResponseRepository recommendResponseRepository;
    private final ScoreRepository scoreRepository;
    private final CategoryRepository categoryRepository;

    /**
     * GPT를 통해 질문 목록 생성
     */
    @GetMapping("/questions")
    public ResponseEntity<List<String>> generateQuestions(HttpSession session) {
        List<AnswerItemDTO> items = recommendationService.generateQuestionItems();
        session.setAttribute("questionItems", items);

        List<String> questions = items.stream()
                .map(AnswerItemDTO::getQuestion)
                .toList();

        return ResponseEntity.ok(questions);
    }

    /**
     * 사용자 응답을 받고 추천 결과 반환
     */
    @PostMapping("/submit")
    public ResponseEntity<RecommendationResultDTO> submitAnswers(
            @RequestHeader("user-id") String userId,
            @RequestBody AnswerRequestDTO answerRequestDTO,
            HttpSession session
    ) {
        // 세션에서 질문-카테고리 매핑 복원
        List<AnswerItemDTO> original = (List<AnswerItemDTO>) session.getAttribute("questionItems");
        Map<String, String> questionToCategory = original.stream()
                .collect(Collectors.toMap(AnswerItemDTO::getQuestion, AnswerItemDTO::getCategory));

        // 응답 항목에 category 다시 세팅
        for (AnswerItemDTO item : answerRequestDTO.getAnswers()) {
            item.setCategory(questionToCategory.get(item.getQuestion()));
        }

        // 추천 결과 호출
        RecommendationResultDTO result = recommendationService.getRecommendations(userId, answerRequestDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 훈련용 데이터셋 추출
     */
    @GetMapping("/dataset")
    public ResponseEntity<List<Map<String, Object>>> exportTrainingDataset() {
        List<RecommendResponse> responses = recommendResponseRepository.findAllByUsersIdLike("gpt_user_%");

        Map<String, List<RecommendResponse>> grouped = responses.stream()
                .collect(Collectors.groupingBy(RecommendResponse::getUsersId));

        List<Map<String, Object>> dataset = new ArrayList<>();

        for (Map.Entry<String, List<RecommendResponse>> entry : grouped.entrySet()) {
            List<RecommendResponse> resList = entry.getValue();

            for (RecommendResponse res : resList) {
                Map<String, Object> row = new HashMap<>();
                row.put("input", res.getCategory() + ": " + res.getQuestion() + " [SEP] " + res.getAnswer());

                Map<String, Integer> scores = scoreRepository.findByUsersId(entry.getKey())
                        .stream()
                        .collect(Collectors.toMap(
                                s -> categoryRepository.findById(s.getCategoryId())
                                        .orElseThrow(() -> new IllegalArgumentException("잘못된 categoryId: " + s.getCategoryId()))
                                        .getCategory(),
                                Score::getScoreValue
                        ));

                row.put("labels", scores);
                dataset.add(row);
            }
        }

        return ResponseEntity.ok(dataset);
    }
}
