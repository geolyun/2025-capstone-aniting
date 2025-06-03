package com.example.aniting.recommendation;

import com.example.aniting.dto.AnswerItemDTO;
import com.example.aniting.dto.RecommendationDTO;
import com.example.aniting.dto.RecommendationResultDTO;
import com.example.aniting.entity.Pet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendationPrompt {

    public static String buildQuestionPrompt() {
        return "당신은 반려동물 추천 전문가입니다. 사용자의 성향을 파악하기 위해 " +
                "activity, sociability, care, emotional_bond, environment, routine 기준을 모두 포함하여 10~20개의 질문을 JSON 배열 형태로 출력하세요." +
                "\n질문은 예/아니오가 아닌 서술형 또는 짧은 문장으로 답할 수 있도록 작성하세요." +
                "\n각 질문은 다음 형식의 객체로 구성되어야 합니다: {\"question\": 질문, \"category\": 성향기준 }";
    }

    public static String buildRecommendationPrompt(List<AnswerItemDTO> answers) {
        JSONArray arr = new JSONArray();
        for (AnswerItemDTO item : answers) {
            JSONObject obj = new JSONObject();
            obj.put("question", item.getQuestion());
            obj.put("answer", item.getAnswer());
            arr.put(obj);
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("당신은 반려동물 추천 전문가입니다.\n")
                .append("아래 사용자 응답을 참고하여 성향 점수(activity, sociability, care, emotional_bond, environment, routine)를 각각 1~5점으로 평가해주세요.\n")
                .append("각 점수는 JSON 객체의 user_scores 필드로만 출력하세요.\n")
                .append("예시:\n")
                .append("{\n")
                .append("  \"user_scores\": {\n")
                .append("    \"activity\": 3,\n")
                .append("    \"sociability\": 2,\n")
                .append("    \"care\": 4,\n")
                .append("    \"emotional_bond\": 3,\n")
                .append("    \"environment\": 4,\n")
                .append("    \"routine\": 2\n")
                .append("  }\n")
                .append("}\n\n")
                .append(arr.toString());

        return prompt.toString();
    }

    public static List<String> parseQuestionList(String gptResponse) {
        String clean = extractJsonArrayOnly(gptResponse);
        clean = clean.trim();

        // 'clean'이 실제로 배열 텍스트가 아니라면 빈 리스트를 리턴
        if (!clean.startsWith("[")) {
            return new ArrayList<>();
        }

        JSONArray array = new JSONArray(clean);
        List<String> questions = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String question = obj.optString("question", "").trim();
            if (!question.isEmpty()) {
                questions.add(question);
            }
        }

        return questions;
    }

    public static List<AnswerItemDTO> parseQuestionItems(String gptResponse) {
        String clean = extractJsonArrayOnly(gptResponse);
        clean = clean.trim();

        if (!clean.startsWith("[")) {
            return new ArrayList<>();
        }

        JSONArray array = new JSONArray(clean);
        List<AnswerItemDTO> items = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String question = obj.optString("question", "").trim();
            String category = obj.optString("category", "").trim();
            if (!question.isEmpty() && !category.isEmpty()) {
                AnswerItemDTO item = new AnswerItemDTO();
                item.setQuestion(question);
                item.setCategory(category);
                items.add(item);
            }
        }

        return items;
    }


    public static RecommendationResultDTO parseGptResponse(String gptResponse) {
        String clean = extractJsonObjectOnly(gptResponse).trim();
        RecommendationResultDTO dto = new RecommendationResultDTO();

        if (!clean.startsWith("{")) {
            dto.setUserScores(new HashMap<>());
            dto.setRecommendations(new ArrayList<>());
            return dto;
        }

        JSONObject obj = new JSONObject(clean);
        // 1) user_scores 파싱
        Map<String, Integer> userScores = new HashMap<>();
        if (obj.has("user_scores")) {
            JSONObject scores = obj.getJSONObject("user_scores");
            for (String key : scores.keySet()) {
                userScores.put(key, scores.getInt(key));
            }
        }
        dto.setUserScores(userScores);

        // 2) recommendations 필드는 아예 비워 둔다.
        //    (이전 GPT 추천이 아니라 코드 거리 계산으로 뽑을 것이므로)
        dto.setRecommendations(new ArrayList<>());

        return dto;
    }

    private static String extractJsonArrayOnly(String gptResponse) {
        if (gptResponse == null || gptResponse.isBlank()) {
            return "[]";
        }

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

    private static String extractJsonObjectOnly(String gptResponse) {
        if (gptResponse == null || gptResponse.isBlank()) {
            return "{}";
        }

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