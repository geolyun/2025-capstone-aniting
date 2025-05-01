package com.example.aniting.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CATEGORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID")
    private Long categoryId; // 카테고리 고유 ID

    @Column(name = "CATEGORY", nullable = false, length = 50)
    private String category; // 질문에 대한 항목

    @Column(name = "SCORE_STANDARD", nullable = false, length = 255)
    private String scoreStandard; // 점수 부여 기준

    @Column(name = "STANDARD_DESCRIPTION", nullable = false, length = 1000)
    private String standardDescription; // 점수 부여 기준에 관한 설명
}
