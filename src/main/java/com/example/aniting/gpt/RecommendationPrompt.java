package com.example.aniting.gpt;

import com.example.aniting.dto.*;
import org.json.*;
import java.util.*;

public class RecommendationPrompt {

    public static String buildPrompt(Map<String, String> responses) {
        JSONObject responseJson = new JSONObject(responses);
        return "당신은 반려동물 추천 전문가입니다. 아래는 사용자의 6가지 성향 점수입니다.\n" +
                "각 성향마다 1~5점 사이의 점수를 바탕으로 사용자에게 가장 적합한 반려동물 3가지를 추천하고, 각 반려동물에 대한 추천 이유를 작성해주세요.\n" +
                "참고: 사용자 응답이 모호하거나 애매할 경우에는 점수를 높게 주지 마세요.\n" +
                "가능하면 점수는 2~3점 중심으로 분포되도록 설정해주세요.\n\n" +
                "[성향 점수 항목 설명]\n" +
                "- activity: 활동성\n" +
                "- sociability: 사회성\n" +
                "- care: 돌봄 의지\n" +
                "- emotional_bond: 정서적 교감\n" +
                "- environment: 환경 적합성\n" +
                "- routine: 일상 루틴\n\n" +
                "출력은 반드시 다음 JSON 형식을 따르세요:\n\n" +
                "{\n" +
                "  \"user_scores\": {\n" +
                "    \"activity\": 숫자,\n" +
                "    \"sociability\": 숫자,\n" +
                "    \"care\": 숫자,\n" +
                "    \"emotional_bond\": 숫자,\n" +
                "    \"environment\": 숫자,\n" +
                "    \"routine\": 숫자\n" +
                "  },\n" +
                "  \"recommendations\": [\n" +
                "    {\n" +
                "      \"rank\": 1,\n" +
                "      \"animal\": \"동물 이름\",\n" +
                "      \"reason\": \"추천 이유\"\n" +
                "    }, { ... }, { ... }\n" +
                "  ]\n" +
                "}\n\n" +
                "사용자 응답: " + responseJson.toString();
    }

    public static RecommendationResultDTO parseGptResponse(String gptResponse) {
        RecommendationResultDTO result = new RecommendationResultDTO();

        try {
            // ```json 제거
            String clean = gptResponse.replaceAll("```json", "").replaceAll("```", "").trim();
            JSONObject parsed = new JSONObject(clean);

            JSONObject scoreJson = parsed.getJSONObject("user_scores");
            Map<String, Integer> userScores = new HashMap<>();
            for (String key : scoreJson.keySet()) {
                userScores.put(key, scoreJson.getInt(key));
            }
            result.setUserScores(userScores);

            JSONArray recArray = parsed.getJSONArray("recommendations");
            List<RecommendationDTO> recommendations = new ArrayList<>();
            for (int i = 0; i < recArray.length(); i++) {
                JSONObject rec = recArray.getJSONObject(i);
                RecommendationDTO dto = new RecommendationDTO();
                dto.setRank(rec.getInt("rank"));
                dto.setAnimal(rec.getString("animal"));
                dto.setReason(rec.getString("reason"));
                recommendations.add(dto);
            }
            result.setRecommendations(recommendations);

        } catch (Exception e) {
            throw new RuntimeException("GPT 응답 파싱 실패: " + e.getMessage());
        }

        return result;
    }
}

