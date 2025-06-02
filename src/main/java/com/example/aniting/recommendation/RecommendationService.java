package com.example.aniting.recommendation;

import com.example.aniting.ai.OpenAiClient;
import com.example.aniting.dto.AnswerItemDTO;
import com.example.aniting.dto.AnswerRequestDTO;
import com.example.aniting.dto.RecommendationDTO;
import com.example.aniting.dto.RecommendationResultDTO;
import com.example.aniting.entity.*;
import com.example.aniting.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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

    public List<AnswerItemDTO> generateQuestionItems() {
        String prompt = RecommendationPrompt.buildQuestionPrompt();
        String gptResponse = openAiClient.callGPTAPI(prompt);
        return RecommendationPrompt.parseQuestionItems(gptResponse);
    }

    public RecommendationResultDTO getRecommendations(String userId, AnswerRequestDTO requestDto) {
        initCategoryDataIfEmpty();
        List<AnswerItemDTO> answers = requestDto.getAnswers();
        List<String> excludedPetNames = petRepository.findAllPetNames();
        List<Pet> similarPets = fetchSimilarPets(excludedPetNames);

        String prompt = RecommendationPrompt.buildRecommendationPrompt(answers, excludedPetNames, similarPets);
        String gptResponse = openAiClient.callGPTAPI(prompt);
        RecommendationResultDTO result = RecommendationPrompt.parseGptResponse(gptResponse);

        saveAllRecommendationData(userId, answers, result, prompt, gptResponse);
        return result;
    }

    public void saveAllRecommendationData(
            String userId,
            List<AnswerItemDTO> answers,
            RecommendationResultDTO result,
            String prompt,
            String gptResponse
    ) {
        saveRecommendResponses(userId, answers);
        saveUserScores(userId, result.getUserScores());
        saveRecommendHistory(userId, result.getRecommendations());
        saveRecommendLog(userId, prompt, gptResponse);
    }

    private void saveRecommendResponses(String userId, List<AnswerItemDTO> answers) {
        List<RecommendResponse> list = new ArrayList<>();
        int order = 1;
        LocalDateTime now = LocalDateTime.now();

        for (AnswerItemDTO item : answers) {
            RecommendResponse r = new RecommendResponse();
            r.setUsersId(userId);
            r.setQuestionOrder(order++);
            r.setQuestion(item.getQuestion());
            r.setCategory(item.getCategory());
            r.setAnswer(item.getAnswer());
            r.setCreatedAt(now);
            list.add(r);
        }
        recommendResponseRepository.saveAll(list);
    }

    private void saveUserScores(String userId, Map<String, Integer> scores) {
        Map<String, Long> categoryMap = Map.of(
                "activity", 1L, "sociability", 2L, "care", 3L,
                "emotional_bond", 4L, "environment", 5L, "routine", 6L
        );
        List<Score> scoreList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            Score s = new Score();
            s.setUsersId(userId);
            s.setCategoryId(categoryMap.get(entry.getKey()));
            s.setScoreValue(entry.getValue());
            scoreList.add(s);
        }
        scoreRepository.saveAll(scoreList);
    }

    private void saveRecommendHistory(String userId, List<RecommendationDTO> recs) {
        if (recs.size() < 3) return;
        RecommendHistory h = new RecommendHistory();
        h.setUsersId(userId);
        h.setTop1PetId(resolvePetByName(recs.get(0).getAnimal()));
        h.setTop2PetId(resolvePetByName(recs.get(1).getAnimal()));
        h.setTop3PetId(resolvePetByName(recs.get(2).getAnimal()));
        h.setAiReason(recs.get(0).getReason() + " / " + recs.get(1).getReason() + " / " + recs.get(2).getReason());
        h.setCreatedAt(LocalDateTime.now());
        recommendHistoryRepository.save(h);
    }

    private void saveRecommendLog(String userId, String prompt, String response) {
        RecommendLog log = new RecommendLog();
        log.setUsersId(userId);
        log.setAiPrompt(prompt);
        log.setAiResponse(response);
        log.setCreatedAt(LocalDateTime.now());
        recommendLogRepository.save(log);
    }

    private List<Pet> fetchSimilarPets(List<String> petNames) {
        Set<Pet> similarSet = new HashSet<>();
        for (String name : petNames) {
            petRepository.findByPetNm(name).ifPresent(original -> {
                List<Pet> similar = petRepository.findBySpeciesOrBreed(original.getSpecies(), original.getBreed());
                for (Pet p : similar) {
                    if (!petNames.contains(p.getPetNm())) {
                        similarSet.add(p);
                    }
                }
            });
        }
        return new ArrayList<>(similarSet);
    }

    private Pet resolvePetByName(String name) {
        return petRepository.findByPetNm(name.trim())
                .orElseThrow(() -> new IllegalArgumentException("❗ Pet not found: " + name));
    }

    @PostConstruct
    public void init() {
        initCategoryDataIfEmpty();
        initPetDataIfEmpty(); // ✅ 신규 추가
    }

    @Transactional
    public void initPetDataIfEmpty() {
        if (petRepository.count() == 0) {
            List<Pet> pets = new ArrayList<>();
            pets.add(Pet.builder()
                    .petNm("햄스터")
                    .species("포유류")
                    .breed("골든")
                    .personalityTags("소심함, 야행성, 혼자 지냄")
                    .careLevel("낮음")
                    .isSpecial("N")
                    .categoryIds("1,2,3,4,5,6")
                    .traitScores("2,1,2,2,4,3")
                    .description("공간을 많이 차지하지 않고 관리가 쉬운 소형 반려동물입니다.")
                    .build());

            petRepository.saveAll(pets);
        }
    }

    @Transactional
    void initCategoryDataIfEmpty() {
        if (!categoryRepository.existsById(1L)) {
            List<Category> list = List.of(
                    new Category("activity", "활동성", "하루의 에너지 소비량 및 야외 활동 선호도"),
                    new Category("sociability", "사회성", "다른 사람/동물과의 교류 능력"),
                    new Category("care", "돌봄 의지", "돌봄 시간과 정성에 대한 의지"),
                    new Category("emotional_bond", "정서적 교감", "감정 공유와 유대감 선호도"),
                    new Category("environment", "환경 적합성", "생활 공간 조건 및 특성"),
                    new Category("routine", "일상 루틴", "일과 패턴의 안정성")
            );
            categoryRepository.saveAll(list);
        }
    }
}
