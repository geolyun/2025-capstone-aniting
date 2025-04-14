package com.example.aniting.service;

import com.example.aniting.dto.*;
import com.example.aniting.entity.*;
import com.example.aniting.repository.*;
import com.example.aniting.gpt.OpenAiClient;
import com.example.aniting.gpt.RecommendationPrompt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * 전체 추천 흐름 처리
     */
    public RecommendationResultDTO getRecommendations(String userId, AnswerRequestDTO requestDto) {
        // 1. 프롬프트 생성
        String prompt = RecommendationPrompt.buildPrompt(requestDto.getAnswers());

        // 2. GPT 호출
        String gptResponse = openAiClient.callGPTAPI(prompt);

        // 3. 결과 파싱
        RecommendationResultDTO resultDto = RecommendationPrompt.parseGptResponse(gptResponse);

        // 4. 저장 처리
        saveAllRecommendationData(userId, requestDto, resultDto, prompt, gptResponse);

        return resultDto;
    }

    // 🔽 통합 저장 메서드
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
        initCategoryDataIfEmpty(); // 최초 1회
    }

    private void saveUserScores(String userId, Map<String, Integer> userScores) {
        Map<String, Long> categoryMap = Map.of(
                "activity", 1L,
                "sociability", 2L,
                "care", 3L,
                "emotional_bond", 4L,
                "environment", 5L,
                "routine", 6L
        );

        List<Score> scores = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : userScores.entrySet()) {
            Score score = new Score();
            score.setUserId(userId);
            score.setCategoryId(categoryMap.get(entry.getKey()));
            score.setScoreValue(entry.getValue());
            scores.add(score);
        }
        scoreRepository.saveAll(scores);
    }

    private void saveRecommendedPets(List<RecommendationDTO> recommendations) {
        for (RecommendationDTO rec : recommendations) {
            String petName = rec.getAnimal();

            // 이미 존재하는지 확인
            Optional<Pet> existing = petRepository.findByPetNm(petName.trim());
            if (existing.isEmpty()) {
                Pet newPet = new Pet();
                newPet.setPetNm(petName);
                newPet.setDescription(rec.getReason());  // 간단한 이유를 설명으로 저장
                newPet.setSpecies(rec.getSpecies());  // 종 분류는 별도 처리 가능
                newPet.setBreed(rec.getBreed());
                newPet.setCareLevel(rec.getCareLevel());
                String isSpecial = rec.getIsSpecial();
                if (isSpecial != null) {
                    isSpecial = isSpecial.equalsIgnoreCase("yes") || isSpecial.equalsIgnoreCase("y") ? "Y" : "N";
                } else {
                    isSpecial = "N"; // 기본값
                }
                newPet.setIsSpecial(isSpecial);
                newPet.setCategory(0L); // 매핑 안된 경우 0
                petRepository.save(newPet);
            }
        }
    }

    private void initCategoryData() {
        List<Category> categories = List.of(
                new Category(1L, "activity", "활동성", "사용자의 하루 활동량과 외부 활동 선호도"),
                new Category(2L, "sociability", "사회성", "새로운 환경/사람에 대한 적응력"),
                new Category(3L, "care", "돌봄 의지", "반려동물에게 투자할 시간과 책임감"),
                new Category(4L, "emotional_bond", "정서적 교감", "감정적 유대와 교감에 대한 기대"),
                new Category(5L, "environment", "환경 적합성", "거주 환경의 크기, 소음 등"),
                new Category(6L, "routine", "일상 루틴", "하루의 에너지 흐름, 규칙성")
        );
        categoryRepository.saveAll(categories);
    }


    private void saveRecommendResponses(String userId, AnswerRequestDTO requestDto) {
        List<RecommendResponse> responses = new ArrayList<>();
        int order = 1;
        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<String, String> entry : requestDto.getAnswers().entrySet()) {
            RecommendResponse response = new RecommendResponse();
            response.setUserId(userId);
            response.setQuestionOrder(order++);
            response.setQuestion(entry.getKey());
            response.setAnswer(entry.getValue());
            response.setCreatedAt(now);
            responses.add(response);
        }

        recommendResponseRepository.saveAll(responses);
    }

    private void saveRecommendLog(String userId, String prompt, String aiResponse) {
        RecommendLog log = new RecommendLog();
        log.setUserId(userId);
        log.setAiPrompt(prompt);
        log.setAiResponse(aiResponse);
        log.setCreatedAt(LocalDateTime.now());

        recommendLogRepository.save(log);
    }

    private void saveRecommendHistory(String userId, List<RecommendationDTO> recommendations) {
        if (recommendations.size() < 3) return; // 최소 3개 추천이 있을 때만 저장

        RecommendHistory history = new RecommendHistory();
        history.setUserId(userId);
        history.setTop1PetId(resolvePetIdByName(recommendations.get(0).getAnimal()));
        history.setTop2PetId(resolvePetIdByName(recommendations.get(1).getAnimal()));
        history.setTop3PetId(resolvePetIdByName(recommendations.get(2).getAnimal()));
        history.setAiReason(recommendations.get(0).getReason()); // 대표 이유 1개만 저장
        history.setCreatedAt(LocalDateTime.now());

        recommendHistoryRepository.save(history);
    }

    // 6. 카테고리 정보 초기화
    private void initCategoryDataIfEmpty() {
        if (categoryRepository.count() == 0) {
            // 최초에만 삽입
            categoryRepository.saveAll(List.of(
                    new Category(null, "activity", "활동성", "사용자의 하루 활동량과 외부 활동 선호도"),
                    new Category(null, "sociability", "사회성", "새로운 환경/사람에 대한 적응력"),
                    new Category(null, "care", "돌봄 의지", "반려동물에게 투자할 시간과 책임감"),
                    new Category(null, "emotional_bond", "정서적 교감", "감정적 유대와 교감에 대한 기대"),
                    new Category(null, "environment", "환경 적합성", "거주 환경의 크기, 소음 등"),
                    new Category(null, "routine", "일상 루틴", "하루의 에너지 흐름, 규칙성")
            ));
        }
    }


    /**
     * Pet 이름으로 ID 조회 (DB 기반)
     */
    private Long resolvePetIdByName(String name) {
        return petRepository.findByPetNm(name.trim())
                .map(Pet::getPetId)
                .orElse(0L);
    }
}
