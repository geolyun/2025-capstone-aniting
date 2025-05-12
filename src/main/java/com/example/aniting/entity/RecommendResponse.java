package com.example.aniting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "RECOMMEND_RESPONSE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESPONSE_ID")
    private Long responseId; // AI 응답 고유 ID

    @Column(name = "USERS_ID", nullable = false, length = 20)
    private String usersId; // 사용자 ID (USER 참조)

    @Column(name = "QUESTION_ORDER", nullable = false)
    private Integer questionOrder; // 질문 순서

    @Column(name = "QUESTION", nullable = false, columnDefinition = "TEXT")
    private String question; // AI가 한 질문

    @Column(name = "CATEGORY")
    private String category;

    @Column(name = "ANSWER", nullable = false, columnDefinition = "TEXT")
    private String answer; // 사용자 응답

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt; // 응답 시각
}

