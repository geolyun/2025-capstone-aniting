package com.example.aniting.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PET")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PET_ID")
    private Long petId; // 반려동물 고유 ID

    @Column(name = "PET_NM", nullable = false, length = 100)
    private String petNm; // 동물 이름

    @Column(name = "SPECIES", nullable = true, length = 50)
    private String species; // 동물 종 (강아지, 고양이 등)

    @Column(name = "BREED", length = 100)
    private String breed; // 품종

    @Column(name = "PERSONALITY_TAGS", length = 255)
    private String personalityTags; // 성격 태그 (콤마 구분)

    @Column(name = "CARE_LEVEL", length = 50)
    private String careLevel; // 관리 난이도

    @Column(name = "IS_SPECIAL", length = 1)
    private String isSpecial; // 특수동물 여부 (Y/N)

    @Column(name = "CATEGORY_IDS", length = 50)
    private String categoryIds;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description; // 설명 텍스트
}

