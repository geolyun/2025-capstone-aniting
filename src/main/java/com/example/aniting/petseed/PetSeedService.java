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

    public void generateAndSavePets() {
        String prompt = PetPrompt.buildPetSeedPrompt();
        String response = openAiClient.callGPTAPI(prompt);

        JSONArray array = extractPetArray(response);
        if (array.isEmpty()) {
            log.warn("❗ GPT 응답이 비어 있거나 파싱 실패: \n{}", response);
            return;
        }

        // 1) DB에 이미 있는 petNm과 species+breed 조합을 미리 캐싱
        Set<String> existingNames = petRepository.findAllPetNamesSet();
        Set<String> existingSpecBreed = petRepository.findAll()
                .stream()
                .map(p -> p.getSpecies().trim() + ":" + p.getBreed().trim())
                .collect(Collectors.toSet());

        // 2) 새로 저장할 때 중복 방지용 로컬 Set
        Set<String> newNames = new HashSet<>();
        Set<String> newSpecBreed = new HashSet<>();

        List<Pet> newPets = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String petName = obj.getString("animal").trim();
            String species = obj.optString("species", "").trim();
            String breed = obj.optString("breed", "").trim();

            String specBreedKey = species + ":" + breed;

            // 3) DB 또는 같은 배치 내에서 이미 있으면 skip
            if (existingNames.contains(petName) || newNames.contains(petName)) {
                continue;
            }
            if (existingSpecBreed.contains(specBreedKey) || newSpecBreed.contains(specBreedKey)) {
                continue;
            }

            // 4) 신규로 저장 대기
            Pet pet = Pet.builder()
                    .petNm(petName)
                    .species(species)
                    .breed(breed)
                    .personalityTags(obj.optString("personality_tags", ""))
                    .careLevel(obj.optString("care_level", "중간"))
                    .isSpecial(obj.optString("is_special", "N"))
                    .traitScores(obj.optString("trait_scores", "3,3,3,3,3,3"))
                    .description(obj.optString("reason", ""))
                    .categoryIds("1,2,3,4,5,6")
                    .build();

            newPets.add(pet);
            newNames.add(petName);
            newSpecBreed.add(specBreedKey);
        }

        petRepository.saveAll(newPets);
        log.info("✅ 반려동물 데이터 저장 완료: {}건 추가됨", newPets.size());
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