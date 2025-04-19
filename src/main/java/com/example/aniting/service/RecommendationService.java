package com.example.aniting.service;

import com.example.aniting.dto.AnswerRequestDTO;
import com.example.aniting.dto.RecommendationDTO;
import com.example.aniting.dto.RecommendationResultDTO;
import com.example.aniting.entity.*;
import com.example.aniting.gpt.OpenAiClient;
import com.example.aniting.gpt.RecommendationPrompt;
import com.example.aniting.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final OpenAiClient openAiClient;
    private final RecommendResponseRepository recommendResponseRepository;
    private final ScoreRepository scoreRepository;
    private final RecommendHistoryRepository recommendHistoryRepository;
    private final RecommendLogRepository recommendLogRepository;
    private final PetRepository petRepository;
    private final CategoryRepository categoryRepository;

    public RecommendationResultDTO getRecommendations(String userId, AnswerRequestDTO responses) {
        String prompt = RecommendationPrompt.buildPrompt(responses.getAnswers());
        String gptResponse = openAiClient.callGPTAPI(prompt);
        RecommendationResultDTO result = RecommendationPrompt.parseGptResponse(gptResponse);


        saveAllRecommendationData(userId, responses, result, prompt, gptResponse);
        return result;
    }

    public void saveAllRecommendationData(
            String userId,
            AnswerRequestDTO requestDto,
            RecommendationResultDTO resultDto,
            String prompt,
            String aiResponse
    ) {
        saveRecommendResponses(userId, requestDto);
        saveUserScores(userId, resultDto.getUserScores());
        saveRecommendedPets(resultDto.getRecommendations());
        saveRecommendHistory(userId, resultDto.getRecommendations());
        saveRecommendLog(userId, prompt, aiResponse);
        initCategoryDataIfEmpty();
    }

    private void saveRecommendResponses(String usersId, AnswerRequestDTO requestDto) {
        List<RecommendResponse> responses = new ArrayList<>();
        int order = 1;
        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<String, String> entry : requestDto.getAnswers().entrySet()) {
            RecommendResponse res = new RecommendResponse();
            res.setUsersId(usersId);
            res.setQuestionOrder(order++);
            res.setQuestion(entry.getKey());
            res.setAnswer(entry.getValue());
            res.setCreatedAt(now);
            responses.add(res);
        }

        recommendResponseRepository.saveAll(responses);
    }

    private void saveUserScores(String usersId, Map<String, Integer> scores) {
        Map<String, Long> categoryMap = Map.of(
                "activity", 1L,
                "sociability", 2L,
                "care", 3L,
                "emotional_bond", 4L,
                "environment", 5L,
                "routine", 6L
        );

        List<Score> scoreEntities = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            Score s = new Score();
            s.setUsersId(usersId);
            s.setCategoryId(categoryMap.get(entry.getKey()));
            s.setScoreValue(entry.getValue());
            scoreEntities.add(s);
        }

        scoreRepository.saveAll(scoreEntities);
    }

    private void saveRecommendedPets(List<RecommendationDTO> recommendations) {
        for (RecommendationDTO rec : recommendations) {
            String name = rec.getAnimal().trim();

            boolean exists = petRepository.findByPetNm(name).isPresent();
            if (!exists) {
                Pet pet = new Pet();
                pet.setPetNm(name);
                pet.setSpecies(rec.getSpecies());
                pet.setBreed(rec.getBreed());

                // 🟡 돌봄 난이도 & 특이 품종 처리
                pet.setCareLevel(
                        List.of("낮음", "중간", "높음").contains(rec.getCareLevel()) ? rec.getCareLevel() : "중간"
                );

                String isSpecial = rec.getIsSpecial();
                if (isSpecial != null) {
                    isSpecial = isSpecial.equalsIgnoreCase("yes") || isSpecial.equalsIgnoreCase("y") ? "Y" : "N";
                } else {
                    isSpecial = "N";
                }
                pet.setIsSpecial(isSpecial);

                pet.setDescription(rec.getReason());
                pet.setCategory(0L);
                pet.setPersonalityTags(null);

                petRepository.save(pet);
            }
        }
    }

    private void saveRecommendHistory(String usersId, List<RecommendationDTO> recs) {
        if (recs.size() < 3) return;

        RecommendHistory history = new RecommendHistory();
        history.setUsersId(usersId);
        history.setTop1Pet(resolvePetByName(recs.get(0).getAnimal()));
        history.setTop2Pet(resolvePetByName(recs.get(1).getAnimal()));
        history.setTop3Pet(resolvePetByName(recs.get(2).getAnimal()));
        history.setAiReason(recs.get(0).getReason());
        history.setCreatedAt(LocalDateTime.now());

        recommendHistoryRepository.save(history);
    }

    private void saveRecommendLog(String usersId, String prompt, String aiResponse) {
        RecommendLog log = new RecommendLog();
        log.setUsersId(usersId);
        log.setAiPrompt(prompt);
        log.setAiResponse(aiResponse);
        log.setCreatedAt(LocalDateTime.now());
        recommendLogRepository.save(log);
    }

    private void initCategoryDataIfEmpty() {
        if (categoryRepository.count() == 0) {
            List<Category> categories = List.of(
                    new Category(null, "activity", "활동성", "하루의 에너지 소비량 및 야외 활동 선호도"),
                    new Category(null, "sociability", "사회성", "다른 사람/동물과의 교류 능력"),
                    new Category(null, "care", "돌봄 의지", "돌봄 시간과 정성에 대한 의지"),
                    new Category(null, "emotional_bond", "정서적 교감", "감정 공유와 유대감 선호도"),
                    new Category(null, "environment", "환경 적합성", "생활 공간 조건 및 특성"),
                    new Category(null, "routine", "일상 루틴", "일과 패턴의 안정성")
            );
            categoryRepository.saveAll(categories);
        }
    }

    private Pet resolvePetByName(String name) {
        return petRepository.findByPetNm(name.trim())
                .orElseThrow(() -> new IllegalArgumentException("❗ 해당 이름의 Pet을 찾을 수 없습니다: " + name));
    }

}
