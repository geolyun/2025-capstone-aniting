package com.example.aniting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "RECOMMEND_LOG")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOG_ID")
    private Long logId; // 로그 고유 ID

    @Column(name = "USERS_ID", nullable = false, length = 20)
    private String usersId; // 사용자 ID (USER 참조)

    @Column(name = "AI_PROMPT", nullable = false, columnDefinition = "TEXT")
    private String aiPrompt; // AI에게 보낸 프롬프트 전체

    @Column(name = "AI_RESPONSE", nullable = false, columnDefinition = "TEXT")
    private String aiResponse; // AI의 응답 전체

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt; // 생성 시각
}

