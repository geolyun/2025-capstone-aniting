// Refactored RecommendationPromptTest.java
package com.example.aniting.recommendation;

import com.example.aniting.dto.AnswerItemDTO;
import com.example.aniting.dto.RecommendationResultDTO;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationPromptTest {

    @Test
    void buildQuestionPrompt_containsKeyPhrases() {
        String prompt = RecommendationPrompt.buildQuestionPrompt();
        assertTrue(prompt.contains("반드시 JSON 배열만 반환하세요"));
        assertTrue(prompt.contains("6개 기준"));
        assertTrue(prompt.contains("한글로 작성"));
    }

    @Test
    void parseQuestionItems_parsesValidJsonArray() {
        String json = """
            [
              {"question": "당신의 하루 일과는 어떻게 되시나요?", "category": "routine"},
              {"question": "혼자 있는 걸 선호하시나요?", "category": "sociability"}
            ]
        """;
        List<AnswerItemDTO> result = RecommendationPrompt.parseQuestionItems(json);
        assertEquals(2, result.size());
        assertEquals("당신의 하루 일과는 어떻게 되시나요?", result.get(0).getQuestion());
        assertEquals("routine", result.get(0).getCategory());
    }

    @Test
    void parseQuestionItems_handlesJsonBlock() {
        String json = """
            ```json
            [
              {"question": "예상치 못한 상황에 어떻게 대처하시나요?", "category": "emotional_bond"}
            ]
            ```
        """;
        List<AnswerItemDTO> result = RecommendationPrompt.parseQuestionItems(json);
        assertEquals(1, result.size());
        assertEquals("예상치 못한 상황에 어떻게 대처하시나요?", result.get(0).getQuestion());
    }

    @Test
    void parseQuestionItems_returnsEmptyOnNull() {
        List<AnswerItemDTO> result = RecommendationPrompt.parseQuestionItems(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void parseGptResponse_returnsStructuredDto() {
        String json = """
        {
          "user_scores": {
            "activity": 3,
            "sociability": 3,
            "care": 3,
            "emotional_bond": 3,
            "environment": 3,
            "routine": 3
          },
          "recommendations": [
            {
              "rank": 1,
              "animal": "햄스터",
              "species": "포유류",
              "breed": "시리아",
              "care_level": "중간",
              "is_special": "N",
              "trait_scores": "3,3,3,3,3,3",
              "reason": "작고 돌보기 쉬움"
            }
          ]
        }
        """;
        RecommendationResultDTO dto = RecommendationPrompt.parseGptResponse(json);
        assertEquals(3, dto.getUserScores().get("activity"));
        assertEquals("햄스터", dto.getRecommendations().get(0).getAnimal());
        assertEquals("3,3,3,3,3,3", dto.getRecommendations().get(0).getTraitScores());
    }
}
