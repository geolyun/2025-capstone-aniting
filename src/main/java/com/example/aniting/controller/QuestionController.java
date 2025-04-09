package com.example.aniting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/question/")
public class QuestionController {

    @GetMapping
    public ResponseEntity<List<String>> getQuestions() {
        List<String> questions = List.of(
                "집에서 시간을 보낼 때 좋아하는 활동이 있다면 어떤 건가요?",
                "낯선 사람이나 새로운 환경에 얼마나 쉽게 적응하시나요?",
                "반려동물을 키우게 된다면 하루 중 어느 정도 시간을 함께 보내고 싶으신가요?",
                "거주 공간에 대해 간단히 설명해 주실 수 있나요? (예: 크기, 조용한지 여부 등)",
                "반려동물과 함께 하고 싶은 활동이 있다면 어떤 것이 있나요?",
                "예상치 못한 상황이 발생했을 때 유연하게 대처하는 편이신가요?",
                "하루 중 가장 에너지가 많고 활발한 시간대는 언제인가요?",
                "장시간 집을 비우는 일이 자주 있나요? 있다면 어떤 이유로 비우시나요?",
                "반려동물에게 바라는 점이나 기대하는 역할이 있다면 무엇인가요?");

        return ResponseEntity.ok(questions);
    };

}
