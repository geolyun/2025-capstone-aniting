package com.example.aniting.recommendation;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RecommendationResponsePrompt {

	public static String buildAnswerPrompt(String question) {
	    return "당신은 반려동물을 키워본 적 없는 일반 사용자입니다." +
	    	   "아래 질문에 대해 당신의 성격과 생활 스타일에 맞춰 자연스럽고 짧은 답변을 작성해 주세요. " +
	           "답변은 너무 길거나 과도하게 설명하지 말고, 일상적인 말투로 작성하세요." +
	    	   "모든 답변은 한 명의 사용자가 응답하는 것처럼 일관성 있게 유지해 주세요.\n\n" +
	           "질문: " + question + "\n답변:";
	}

    public static List<String> parseQuestionList(String jsonResponse) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonResponse, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new RuntimeException("질문 리스트 파싱 실패: " + jsonResponse, e);
        }
    }
	
}
