package com.example.aniting.recommendation;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RecommendationResponsePrompt {

	public static String buildQuestionPrompt() {
        return "사용자의 반려동물 성향을 파악할 수 있는 질문 9~11개를 생성해 주세요. " +
               "각 질문은 1개의 문장으로 자연스럽게 작성해 주세요. 결과는 JSON 배열로 반환해 주세요.";
    }

	public static String buildAnswerPrompt(String question) {
	    return "다음 질문에 대해 일반 사용자가 짧고 자연스럽게 대답한 것처럼 간단한 한두 문장으로 답변을 생성해 주세요. 너무 길거나 과도하게 설명하지 말고, 일상적인 말투를 사용하세요.\n\n" +
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
