package com.example.aniting.petseed;

import com.example.aniting.ai.OpenAiClient;
import com.example.aniting.entity.Pet;
import com.example.aniting.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetSeedService {

    private final PetRepository petRepository;
    private final OpenAiClient openAiClient;

    @Transactional
    public void generateAndSavePets() {
        log.info("▶ PetSeedService.generateAndSavePets() 시작");

        // ① PetPrompt 확인
        String prompt = PetPrompt.buildPetSeedPrompt();
        log.debug("   └ PetPrompt.buildPetSeedPrompt() → {}", prompt);

        // ② GPT 호출 및 원본 응답
        String response = openAiClient.callGPTAPI(prompt);
        log.debug("   └ GPT raw response:\n{}", response);

        // ③ JSON 파싱
        JSONArray array = extractPetArray(response);
        log.info("   └ extractPetArray → length = {}", array.length());

        List<Pet> newPets = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String petName = obj.getString("animal");
            log.debug("     · 파싱된 animal = {}", petName);

            // 중복 검사
            if (petRepository.findByPetNm(petName).isPresent()) {
                log.debug("       ↳ 이미 존재해서 스킵: {}", petName);
                continue;
            }

            Pet pet = Pet.builder()
                    .petNm(petName)
                    .species(obj.optString("species", ""))
                    .breed(obj.optString("breed", ""))
                    .personalityTags(obj.optString("personality_tags", ""))
                    .careLevel(obj.optString("care_level", "중간"))
                    .isSpecial(obj.optString("is_special", "N"))
                    .traitScores(obj.optString("trait_scores", "3,3,3,3,3,3"))
                    .description(obj.optString("reason", ""))
                    .categoryIds("1,2,3,4,5,6")
                    .build();

            newPets.add(pet);
            log.debug("       ↳ 신규 추가 예정: {}", petName);
        }

        if (newPets.isEmpty()) {
            log.warn("   ⚠️ 저장할 새 반려동물이 없습니다. (newPets.isEmpty())");
        } else {
            petRepository.saveAll(newPets);
            log.info("   ✅ PetSeedService: 반려동물 {}건 저장 완료", newPets.size());
        }
    }

    private JSONArray extractPetArray(String response) {
        String clean = response.replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();
        int start = clean.indexOf("[");
        int end = clean.lastIndexOf("]");
        if (start == -1 || end == -1 || end <= start) return new JSONArray();
        return new JSONArray(clean.substring(start, end + 1));
    }
}
