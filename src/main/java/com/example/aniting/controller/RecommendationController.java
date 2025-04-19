package com.example.aniting.controller;

import com.example.aniting.dto.AnswerRequestDTO;
import com.example.aniting.dto.RecommendationResultDTO;
import com.example.aniting.dto.UsersDTO;
import com.example.aniting.service.RecommendationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping("/recommend")
    public ResponseEntity<RecommendationResultDTO> recommend(
            HttpServletRequest request,
            @RequestBody AnswerRequestDTO responses
    ) {
        HttpSession session = request.getSession(false);

        // 세션이 없거나 로그인 사용자 정보가 없는 경우
        if (session == null || session.getAttribute("user") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 세션에서 UsersDTO 꺼내기
        UsersDTO user = (UsersDTO) session.getAttribute("user");
        String userId = user.getUsersId();

        // 추천 및 저장 로직 호출
        RecommendationResultDTO result = recommendationService.getRecommendations(userId, responses);
        return ResponseEntity.ok(result);
    }


}
