package com.example.aniting.recommendation;

import com.example.aniting.ai.OpenAiClient;
import com.example.aniting.dto.AnswerItemDTO;
import com.example.aniting.dto.AnswerRequestDTO;
import com.example.aniting.dto.RecommendationDTO;
import com.example.aniting.dto.RecommendationResultDTO;
import com.example.aniting.entity.*;
import com.example.aniting.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @InjectMocks private RecommendationService recommendationService;

    @Mock private OpenAiClient openAiClient;
    @Mock private RecommendResponseRepository recommendResponseRepository;
    @Mock private ScoreRepository scoreRepository;
    @Mock private RecommendHistoryRepository recommendHistoryRepository;
    @Mock private RecommendLogRepository recommendLogRepository;
    @Mock private PetRepository petRepository;
    @Mock private CategoryRepository categoryRepository;

    @Test
    // 추천 응답 저장 동작이 잘 수행되는지 확인
    void saveAllRecommendationData_savesRecommendResponses() {
        AnswerItemDTO item = new AnswerItemDTO();
        item.setQuestion("Q1");
        item.setAnswer("A1");

        AnswerRequestDTO req = new AnswerRequestDTO();
        req.setAnswers(List.of(item));

        RecommendationResultDTO dto = new RecommendationResultDTO();
        dto.setUserScores(Map.of(
                "activity", 3, "sociability", 2, "care", 1,
                "emotional_bond", 4, "environment", 3, "routine", 5
        ));
        dto.setRecommendations(Collections.emptyList());

        lenient().when(petRepository.findByPetNm(anyString())).thenReturn(Optional.of(new Pet()));

        recommendationService.saveAllRecommendationData("user1", req, dto, "prompt", "response");

        verify(recommendResponseRepository).saveAll(any());
    }

    @Test
    // GPT 질문 생성 결과가 유효한지 검증
    void generateQuestions_returnsValidQuestions() {
        String gptOutput = "[\"Q1\",\"Q2\"]";
        when(openAiClient.callGPTAPI(anyString())).thenReturn(gptOutput);

        List<String> result = recommendationService.generateQuestions();

        assertEquals(2, result.size());
        assertTrue(result.contains("Q1"));
    }

    @Test
    // GPT 응답 파싱 후 추천 DTO 생성 여부 검증
    void getRecommendations_processesGptAndReturnsDTO() {
        String gptOutput = """
        {
          \"user_scores\": {
            \"activity\": 3,
            \"sociability\": 2,
            \"care\": 1,
            \"emotional_bond\": 4,
            \"environment\": 3,
            \"routine\": 5
          },
          \"recommendations\": [
            {
              \"rank\": 1,
              \"animal\": \"고양이\",
              \"species\": \"포유류\",
              \"breed\": \"페르시안\",
              \"care_level\": \"중간\",
              \"is_special\": \"Y\",
              \"reason\": \"혼자서도 잘 지냄\"
            }
          ]
        }
        """;
        when(openAiClient.callGPTAPI(anyString())).thenReturn(gptOutput);
        when(petRepository.findByPetNm(anyString())).thenReturn(Optional.of(new Pet()));

        AnswerRequestDTO request = new AnswerRequestDTO();
        AnswerItemDTO item = new AnswerItemDTO();
        item.setQuestion("Q1");
        item.setAnswer("A1");
        request.setAnswers(List.of(item));

        RecommendationResultDTO result = recommendationService.getRecommendations("user1", request);
        assertEquals(6, result.getUserScores().size());
        assertEquals("고양이", result.getRecommendations().get(0).getAnimal());
    }

    @Test
    // 점수, 응답, 히스토리, 로그 등 모든 엔티티가 저장되는지 확인
    void saveAllRecommendationData_storesAllEntitiesCorrectly() {
        AnswerItemDTO item = new AnswerItemDTO();
        item.setQuestion("Q1");
        item.setAnswer("A1");

        AnswerRequestDTO req = new AnswerRequestDTO();
        req.setAnswers(List.of(item));

        RecommendationResultDTO dto = new RecommendationResultDTO();
        dto.setUserScores(Map.of(
                "activity", 3,
                "sociability", 2,
                "care", 1,
                "emotional_bond", 4,
                "environment", 3,
                "routine", 5
        ));

        RecommendationDTO rec1 = new RecommendationDTO();
        rec1.setAnimal("고양이");
        rec1.setSpecies("포유류");
        rec1.setBreed("페르시안");
        rec1.setCareLevel("중간");
        rec1.setIsSpecial("Y");
        rec1.setReason("혼자서도 잘 지냄");
        rec1.setRank(1);

        RecommendationDTO rec2 = new RecommendationDTO();
        rec2.setAnimal("강아지");
        rec2.setSpecies("포유류");
        rec2.setBreed("푸들");
        rec2.setCareLevel("높음");
        rec2.setIsSpecial("N");
        rec2.setReason("친화적");
        rec2.setRank(2);

        RecommendationDTO rec3 = new RecommendationDTO();
        rec3.setAnimal("햄스터");
        rec3.setSpecies("포유류");
        rec3.setBreed("골든");
        rec3.setCareLevel("낮음");
        rec3.setIsSpecial("N");
        rec3.setReason("작음");
        rec3.setRank(3);

        dto.setRecommendations(List.of(rec1, rec2, rec3));

        when(categoryRepository.count()).thenReturn(0L);
        when(petRepository.findByPetNm(any())).thenReturn(Optional.of(new Pet()));

        recommendationService.saveAllRecommendationData("user1", req, dto, "prompt", "response");

        verify(categoryRepository).saveAll(any());
        verify(recommendResponseRepository).saveAll(any());
        verify(scoreRepository).saveAll(any());
        verify(recommendHistoryRepository).save(any());
        verify(recommendLogRepository).save(any());
    }
}