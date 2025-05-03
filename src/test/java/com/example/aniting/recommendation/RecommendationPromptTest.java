package com.example.aniting.recommendation;

import com.example.aniting.dto.AnswerItemDTO;
import com.example.aniting.dto.RecommendationResultDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecommendationPromptTest {

    @Test
    // 질문 프롬프트에 필수 문구가 포함되어 있는지 확인
    void buildQuestionPrompt_containsKeyPhrases() {
        String prompt = RecommendationPrompt.buildQuestionPrompt();
        assertTrue(prompt.contains("반드시 JSON 배열만 반환하세요"));
        assertTrue(prompt.contains("6개 기준"));
        assertTrue(prompt.contains("한글로 작성"));
    }

    @Test
    // 추천 프롬프트가 올바른 구조와 질문을 포함하는지 검증
    void buildRecommendationPrompt_containsQuestionsAndStructure() {
        List<AnswerItemDTO> list = List.of(
                new AnswerItemDTO("당신의 하루 활동량은?", "낮음"),
                new AnswerItemDTO("외출을 자주 하나요?", "보통")
        );

        String prompt = RecommendationPrompt.buildRecommendationPrompt(list);
        assertTrue(prompt.contains("user_scores"));
        assertTrue(prompt.contains("recommendations"));
        assertTrue(prompt.contains("당신의 하루 활동량은?"));
        assertTrue(prompt.contains("보통"));
    }

    @Test
    // JSON 배열을 정확히 파싱하는지 검증
    void parseQuestionList_parsesJsonArrayCorrectly() {
        String json = "[\"Q1\", \"Q2\", \"Q3\"]";
        List<String> result = RecommendationPrompt.parseQuestionList(json);
        assertEquals(3, result.size());
        assertEquals("Q1", result.get(0));
        assertEquals("Q2", result.get(1));
    }

    @Test
    // 코드 블럭으로 감싼 JSON 응답도 처리 가능한지 확인
    void parseQuestionList_handlesWrappedJsonBlock() {
        String response = "```json\n[\"Q1\",\"Q2\"]\n```";
        List<String> result = RecommendationPrompt.parseQuestionList(response);
        assertEquals(2, result.size());
        assertTrue(result.contains("Q1"));
    }

    @Test
    // null 응답 시 빈 리스트 반환 확인
    void parseQuestionList_returnsEmptyOnNull() {
        List<String> result = RecommendationPrompt.parseQuestionList(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    // GPT 응답을 RecommendationResultDTO로 변환 가능 여부 확인
    void parseGptResponse_returnsStructuredDto() {
        String json = """
        {
          \"user_scores\": {
            \"activity\": 3,
            \"sociability\": 3,
            \"care\": 3,
            \"emotional_bond\": 3,
            \"environment\": 3,
            \"routine\": 3
          },
          \"recommendations\": [
            {
              \"rank\": 1,
              \"animal\": \"햄스터\",
              \"species\": \"포유류\",
              \"breed\": \"시리아\",
              \"care_level\": \"중간\",
              \"is_special\": \"N\",
              \"reason\": \"작고 돌보기 쉬움\"
            }
          ]
        }
        """;
        RecommendationResultDTO dto = RecommendationPrompt.parseGptResponse(json);
        assertEquals(3, dto.getUserScores().get("activity"));
        assertEquals("햄스터", dto.getRecommendations().get(0).getAnimal());
    }
}
