// Refactored RecommendationServiceTest.java

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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private OpenAiClient openAiClient;
    @Mock
    private RecommendResponseRepository recommendResponseRepository;
    @Mock
    private ScoreRepository scoreRepository;
    @Mock
    private RecommendHistoryRepository recommendHistoryRepository;
    @Mock
    private RecommendLogRepository recommendLogRepository;
    @Mock
    private PetRepository petRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void saveAllRecommendationData_savesRecommendResponses() {
        List<AnswerItemDTO> answers = List.of(new AnswerItemDTO("Q1", "A1", "activity"));

        RecommendationResultDTO dto = new RecommendationResultDTO();
        dto.setUserScores(Map.of(
                "activity", 3, "sociability", 2, "care", 1,
                "emotional_bond", 4, "environment", 3, "routine", 5
        ));
        dto.setRecommendations(Collections.emptyList());

        lenient().when(petRepository.findByPetNm(anyString())).thenReturn(Optional.of(new Pet()));

        recommendationService.saveAllRecommendationData("user1", answers, dto, "prompt", "response");

        verify(recommendResponseRepository).saveAll(any());
    }

    @Test
    void generateQuestions_returnsValidQuestions() {
        String gptOutput = """
                    [
                      {"question": "Q1", "category": "activity"},
                      {"question": "Q2", "category": "care"}
                    ]
                """;
        when(openAiClient.callGPTAPI(anyString())).thenReturn(gptOutput);

        List<AnswerItemDTO> result = recommendationService.generateQuestionItems();

        assertEquals(2, result.size());
        assertEquals("Q1", result.get(0).getQuestion());
    }
}