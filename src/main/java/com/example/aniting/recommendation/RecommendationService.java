package com.example.aniting.recommendation;

import com.example.aniting.dto.AnswerItemDTO;
import com.example.aniting.dto.AnswerRequestDTO;
import com.example.aniting.dto.RecommendationDTO;
import com.example.aniting.dto.RecommendationResultDTO;
import com.example.aniting.entity.*;
import com.example.aniting.ai.OpenAiClient;
import com.example.aniting.recommendation.RecommendationPrompt;
import com.example.aniting.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    public List<String> generateQuestions() {
        String prompt = RecommendationPrompt.buildQuestionPrompt();
        String gptResponse = openAiClient.callGPTAPI(prompt);
        return RecommendationPrompt.parseQuestionList(gptResponse);
    }

    public RecommendationResultDTO getRecommendations(String userId, AnswerRequestDTO responses) {
        String prompt = RecommendationPrompt.buildRecommendationPrompt(responses.getAnswers());
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
        initCategoryDataIfEmpty();
        saveRecommendResponses(userId, requestDto);
        saveUserScores(userId, resultDto.getUserScores());
        saveRecommendedPets(resultDto.getRecommendations());
        saveRecommendHistory(userId, resultDto.getRecommendations());
        saveRecommendLog(userId, prompt, aiResponse);
    }

    private void saveRecommendResponses(String usersId, AnswerRequestDTO requestDto) {
        List<RecommendResponse> responses = new ArrayList<>();
        int order = 1;
        LocalDateTime now = LocalDateTime.now();

        for (AnswerItemDTO item : requestDto.getAnswers()) {
            RecommendResponse res = new RecommendResponse();
            res.setUsersId(usersId);
            res.setQuestionOrder(order++);
            res.setQuestion(item.getQuestion());
            res.setAnswer(item.getAnswer());
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
            if (petRepository.findByPetNm(name).isEmpty()) {
                Pet pet = new Pet();
                pet.setPetNm(name);
                pet.setSpecies(rec.getSpecies());
                pet.setBreed(rec.getBreed());
                pet.setCareLevel(
                        List.of("낮음", "중간", "높음").contains(rec.getCareLevel()) ? rec.getCareLevel() : "중간"
                );

                String isSpecial = rec.getIsSpecial();
                pet.setIsSpecial((isSpecial != null && (isSpecial.equalsIgnoreCase("yes") || isSpecial.equalsIgnoreCase("y"))) ? "Y" : "N");

                pet.setDescription(rec.getReason());
                pet.setCategoryIds("1,2,3,4,5,6");
                pet.setPersonalityTags(null);

                petRepository.save(pet);
            }
        }
    }

    private void saveRecommendHistory(String usersId, List<RecommendationDTO> recs) {
        if (recs.size() < 3) return;

        RecommendHistory history = new RecommendHistory();
        history.setUsersId(usersId);
        history.setTop1PetId(resolvePetByName(recs.get(0).getAnimal()));
        history.setTop2PetId(resolvePetByName(recs.get(1).getAnimal()));
        history.setTop3PetId(resolvePetByName(recs.get(2).getAnimal()));

        StringBuilder aiReasonBuilder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            RecommendationDTO rec = recs.get(i);
            aiReasonBuilder.append(rec.getRank())
                    .append("위: ").append(rec.getAnimal())
                    .append(" - ").append(rec.getReason());
            if (i != 2) aiReasonBuilder.append(" / ");
        }
        history.setAiReason(aiReasonBuilder.toString());
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

    // 카테고리 점수 부여 기준 데이터가 비어있으면 DB에 데이터 삽입
    @Transactional
    synchronized void initCategoryDataIfEmpty() {
        if (!categoryRepository.existsById(1L)) {
            List<Category> categories = List.of(
                new Category("activity", "활동성", "하루의 에너지 소비량 및 야외 활동 선호도"),
                new Category("sociability", "사회성", "다른 사람/동물과의 교류 능력"),
                new Category("care", "돌봄 의지", "돌봄 시간과 정성에 대한 의지"),
                new Category("emotional_bond", "정서적 교감", "감정 공유와 유대감 선호도"),
                new Category("environment", "환경 적합성", "생활 공간 조건 및 특성"),
                new Category("routine", "일상 루틴", "일과 패턴의 안정성")
            );
            categoryRepository.saveAll(categories);
        }
    }

    private Pet resolvePetByName(String name) {
        return petRepository.findByPetNm(name.trim())
                .orElseThrow(() -> new IllegalArgumentException("❗ 해당 이름의 Pet을 찾을 수 없습니다: " + name));
    }
}
