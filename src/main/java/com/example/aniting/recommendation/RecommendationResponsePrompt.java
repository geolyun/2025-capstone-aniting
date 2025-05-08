package com.example.aniting.recommendation;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RecommendationResponsePrompt {

	public static String buildQuestionPrompt() {
	    return "당신은 반려동물 추천 전문가입니다. 현재 반려동물을 키우고 있지 않은 사용자에게 적합한 질문을 9~11개 한글로 생성해 주세요. " +
	           "질문은 사용자의 성향을 파악할 수 있도록 아래 6가지 기준에 따라 작성되어야 합니다: " +
	           "activity, sociability, care, emotional_bond, environment, routine. " +
	           "모든 질문은 반려동물 경험이 없더라도 대답할 수 있는 방식이어야 합니다. " +
	           "질문은 명확하고 직관적으로 1문장씩 구성하세요. " +
	           "반드시 JSON 배열([]) 형식으로만 반환해 주세요.";
	}

	public static String buildAnswerPrompt(String question) {
	    return "아래 질문에 대해, 반려동물을 현재 키우고 있지 않은 일반 사용자가 답하는 것처럼 간단하고 자연스럽게 한두 문장으로 응답을 생성해 주세요. " +
	           "너무 과장되거나 구체적이지 않게, 일반적인 상황에 맞게 작성해 주세요. 일상적인 말투로, 짧게 대답해 주세요.\n\n" +
	           "질문: " + question + "\n답변:";
	}

    public static List<String> parseQuestionList(String jsonResponse) {
        // GPT가 ["질문1", "질문2", ...] 형태로 응답했다고 가정
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonResponse, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new RuntimeException("질문 리스트 파싱 실패: " + jsonResponse, e);
        }
    }
	
}
