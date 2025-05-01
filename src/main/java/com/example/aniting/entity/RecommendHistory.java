package com.example.aniting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "RECOMMEND_HISTORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HISTORY_ID")
    private Long historyId; // 추천 히스토리 고유 ID

    @Column(name = "USERS_ID", nullable = false, length = 20)
    private String usersId; // 사용자 ID (USER 참조)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TOP1_PET_ID", nullable = false)
    private Pet top1PetId; // AI가 추천한 1순위 반려동물 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TOP2_PET_ID")
    private Pet top2PetId; // AI가 추천한 2순위 반려동물 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TOP3_PET_ID")
    private Pet top3PetId; // AI가 추천한 3순위 반려동물 ID

    @Column(name = "AI_REASON", columnDefinition = "TEXT")
    private String aiReason; // AI의 추천 사유

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt; // 생성 시각
}

