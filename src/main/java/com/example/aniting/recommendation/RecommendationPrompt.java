package com.example.aniting.recommendation;

import com.example.aniting.dto.*;
import org.json.*;
import java.util.*;

public class RecommendationPrompt {

    public static String buildPrompt(Map<String, String> responses) {
        JSONObject responseJson = new JSONObject(responses);
        return "당신은 반려동물 추천 전문가입니다. 아래는 사용자 성향에 대한 6가지 점수입니다.\n" +
                "각 성향마다 1~5점 사이의 점수를 바탕으로 사용자에게 가장 적합한 반려동물 3가지를 추천하고, 각 동물의 추천 이유를 작성해주세요.\n\n" +

                "단, 추천 동물은 다음 조건을 반드시 고려하세요:\n" +
                "1. 개와 고양이처럼 흔한 반려동물 외에도 **이국적이거나 특이한 동물**도 되도록이면 하나 이상 포함되도록 하세요.\n" +
                "2. 흔한 반려동물의 여러 가지 종에 대해서도 고려하여 하나 이상은 포함할 수 있도록 하세요.\n" +
                "3. 희귀 품종 또는 특이한 성격의 종도 고려하세요.\n" +
                "4. 단, 실제로 일반인이 실내에서 기를 수 있고, 인간과 정서적 교감을 나눌 수 있는 동물로 제한합니다.\n" +
                "5. 각 추천 동물은 사용자의 성향 점수와 연결하여 구체적인 이유를 작성해주세요.\n\n" +

                "추가로, 추천 동물에 대해 내부적으로 다음과 같은 정보도 함께 구조화해서 포함하세요:\n" +
                "- species: 동물의 종 (예: 포유류, 조류, 파충류 등)\n" +
                "- breed: 품종 이름\n" +
                "- care_level: 돌봄 난이도 (낮음, 중간, 높음)\n" +
                "- is_special: 특이 품종 여부 (\"Y\" 또는 \"N\")\n\n" +

                "참고: 사용자 응답이 모호하거나 애매할 경우에는 점수를 높게 주지 마세요.(2점 이하로 부여)\n" +
                "가능하면 점수는 2~3점 중심으로 분포되도록 설정해주세요.\n\n" +

                "[성향 점수 항목 설명]\n" +
                "- activity: 활동성\n" +
                "- sociability: 사회성\n" +
                "- care: 돌봄 의지\n" +
                "- emotional_bond: 정서적 교감\n" +
                "- environment: 환경 적합성\n" +
                "- routine: 일상 루틴\n\n" +

                "출력은 반드시 다음 JSON 형식을 따르세요. 다른 텍스트는 포함하지 말고 JSON만 반환하세요:\n\n" +
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
                "      \"species\": \"종\",\n" +
                "      \"breed\": \"품종\",\n" +
                "      \"care_level\": \"중간\",\n" +
                "      \"is_special\": \"Yes or NO\",\n" +
                "      \"reason\": \"추천 이유\"\n" +
                "    },\n" +
                "    { ... },\n" +
                "    { ... }\n" +
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
                dto.setSpecies(rec.optString("species", null));
                dto.setBreed(rec.optString("breed", null));
                dto.setCareLevel(rec.optString("care_level", null));
                dto.setIsSpecial(rec.optString("is_special", null));
                recommendations.add(dto);
            }
            result.setRecommendations(recommendations);

        } catch (Exception e) {
            throw new RuntimeException("GPT 응답 파싱 실패: " + e.getMessage());
        }

        return result;
    }
}

