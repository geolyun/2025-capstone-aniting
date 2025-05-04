package com.example.aniting.recommendation;

import com.example.aniting.dto.AnswerItemDTO;
import com.example.aniting.dto.RecommendationDTO;
import com.example.aniting.dto.RecommendationResultDTO;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendationPrompt {

    // 🔵 1. 질문 요청 프롬프트 (questions용)
    public static String buildQuestionPrompt() {
        return "당신은 반려동물 추천 전문가입니다. 사용자의 성향을 파악하기 위해 " +
                "activity, sociability, care, emotional_bond, environment, routine " +
                "6개 기준을 고루 다루는 질문을 9개에서 11개 내의 개수로 한글로 작성해주세요. " +
                "각 질문은 짧고 명확해야 합니다. " +
                "반드시 JSON 배열만 반환하세요. 다른 설명 없이 배열([])만 출력하세요.";
    }

    // 🔵 2. 추천 요청 프롬프트 (submit용)
    public static String buildRecommendationPrompt(List<AnswerItemDTO> answers) {
        JSONArray arr = new JSONArray();
        for (AnswerItemDTO item : answers) {
            JSONObject obj = new JSONObject();
            obj.put("question", item.getQuestion());
            obj.put("answer", item.getAnswer());
            arr.put(obj);
        }
        return "다음은 사용자의 질문과 답변입니다. 이를 참고하여 사용자의 성향 점수(activity, sociability, care, emotional_bond, environment, routine)를 1~5점으로 평가하고, " +
                "가장 적합한 반려동물 3가지를 추천해 주세요.\n" +

                "단, 추천 동물은 다음 조건을 반드시 고려하세요:\n" +
                "1. 개와 고양이처럼 흔한 반려동물 외에도 **이국적이거나 특이한 동물**도 되도록이면 하나 이상 포함되도록 하세요.\n" +
                "2. 희귀 품종 또는 특이한 성격의 종도 고려하세요.\n" +
                "3. 사람들이 혐오감을 느끼거나 꺼려할 수 있는 동물이나 종(예: 파충류)은 배제해주세요.\n" +
                "4. 단, 실제로 일반인이 실내에서 기를 수 있고, 인간과 정서적 교감을 나눌 수 있는 동물로 제한합니다.\n" +
                "5. 각 추천 동물은 사용자의 성향 점수와 연결하여 구체적인 이유를 작성해주세요.\n\n" +

                "추가로, 추천 동물에 대해 내부적으로 다음과 같은 정보도 함께 구조화해서 포함하세요:\n" +
                "- species: 동물의 종 (예: 포유류, 조류, 파충류 등)\n" +
                "- breed: 품종 이름\n" +
                "- care_level: 돌봄 난이도 (낮음, 중간, 높음)\n" +
                "- is_special: 특이 품종 여부 (\"Y\" 또는 \"N\")\n\n" +

                "동물의 종과 품종 이름, 추천해주는 반려동물의 이름은 반드시 한글로 결과를 출력해주세요.\n\n" +

                "참고: 사용자 응답이 모호하거나 애매할 경우에는 점수를 높게 주지 마세요.(2점 이하로 부여)\n" +
                "가능하면 점수는 2~3점 중심으로 분포되도록 설정해주세요.\n\n" +

                "추천 동물마다 species, breed, care_level, is_special, reason을 반드시 포함하세요. " +
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
                arr.toString();
    }

    // 🔵 3. 질문 목록 파싱 (JSONArray 기반)
    public static List<String> parseQuestionList(String gptResponse) {
        String clean = extractJsonArrayOnly(gptResponse);
        JSONArray array = new JSONArray(clean);
        List<String> questions = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            questions.add(array.getString(i));
        }
        return questions;
    }

    // 🔵 4. 추천 결과 파싱 (JSONObject 기반)
    public static RecommendationResultDTO parseGptResponse(String gptResponse) {
        String clean = extractJsonObjectOnly(gptResponse);
        JSONObject obj = new JSONObject(clean);

        RecommendationResultDTO dto = new RecommendationResultDTO();

        // userScores 파싱
        Map<String, Integer> userScores = new HashMap<>();
        JSONObject scores = obj.getJSONObject("user_scores");
        for (String key : scores.keySet()) {
            userScores.put(key, scores.getInt(key));
        }
        dto.setUserScores(userScores);

        // recommendations 파싱
        List<RecommendationDTO> recs = new ArrayList<>();
        JSONArray recArray = obj.getJSONArray("recommendations");
        for (int i = 0; i < recArray.length(); i++) {
            JSONObject rec = recArray.getJSONObject(i);
            RecommendationDTO r = new RecommendationDTO();
            r.setRank(rec.getInt("rank"));
            r.setAnimal(rec.getString("animal"));
            r.setReason(rec.getString("reason"));
            r.setSpecies(rec.optString("species", ""));
            r.setBreed(rec.optString("breed", ""));
            r.setCareLevel(rec.optString("care_level", "중간"));
            r.setIsSpecial(rec.optString("is_special", "N"));
            recs.add(r);
        }
        dto.setRecommendations(recs);

        return dto;
    }

    // 🔵 5. 응답에서 JSON 배열([ ])만 추출
    private static String extractJsonArrayOnly(String gptResponse) {
        if (gptResponse == null || gptResponse.isBlank()) return "[]";

        String clean = gptResponse.replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();

        int startIndex = clean.indexOf("[");
        int endIndex = clean.lastIndexOf("]");

        if (startIndex == -1 || endIndex == -1 || endIndex <= startIndex) {
            return "[]";
        }

        return clean.substring(startIndex, endIndex + 1);
    }

    // 🔵 6. 응답에서 JSON 객체({ })만 추출
    private static String extractJsonObjectOnly(String gptResponse) {
        if (gptResponse == null || gptResponse.isBlank()) return "{}";

        String clean = gptResponse.replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();

        int startIndex = clean.indexOf("{");
        int endIndex = clean.lastIndexOf("}");

        if (startIndex == -1 || endIndex == -1 || endIndex <= startIndex) {
            return "{}";
        }

        return clean.substring(startIndex, endIndex + 1);
    }
}
