package com.example.aniting.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetDTO {
    private Long petId;

    private String petNm;

    private String species;

    private String breed;

    private String personalityTags;

    private String careLevel;

    private String isSpecail;

    private Long category;

    private String description;
}
