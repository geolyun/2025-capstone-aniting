package com.example.aniting.petseed;

import com.example.aniting.ai.OpenAiClient;
import com.example.aniting.entity.Pet;
import com.example.aniting.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetSeedService {

    private final PetRepository petRepository;
    private final OpenAiClient openAiClient;

    public void generateAndSavePets() {
        String prompt = PetPrompt.buildPetSeedPrompt();
        String response = openAiClient.callGPTAPI(prompt);

        JSONArray array = extractPetArray(response);
        if (array.isEmpty()) {
            log.warn("❗ GPT로부터 받은 반려동물 데이터 파싱 실패 또는 빈 배열. 응답:\n{}", response);
            return;
        }

        List<Pet> newPets = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String petName = obj.getString("animal");
            if (petRepository.findByPetNm(petName).isPresent()) {
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
        }

        petRepository.saveAll(newPets);
        log.info("✅ 반려동물 {}건 저장 완료", newPets.size());
    }

    private JSONArray extractPetArray(String response) {
        try {
            String clean = response.replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();
            int start = clean.indexOf("[");
            int end = clean.lastIndexOf("]");
            if (start == -1 || end == -1 || end <= start) {
                return new JSONArray();
            }
            return new JSONArray(clean.substring(start, end + 1));
        } catch (Exception e) {
            log.error("❌ JSON 파싱 중 예외 발생", e);
            return new JSONArray();
        }
    }
}