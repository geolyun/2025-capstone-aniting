package com.example.aniting.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SCORE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCORE_ID")
    private Long scoreId; // 점수 고유 ID

    @Column(name = "USER_ID", nullable = false, length = 20)
    private String userId; // 사용자 ID (USER 참조)

    @Column(name = "CATEGORY_ID", nullable = false)
    private Long categoryId; // 질문에 대한 항목 (CATEGORY 참조)

    @Column(name = "SCORE_VALUE", nullable = false)
    private Integer scoreValue; // 항목별 점수
}

