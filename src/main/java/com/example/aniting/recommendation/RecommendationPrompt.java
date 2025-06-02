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

    public static String buildRecommendationPrompt(
            List<AnswerItemDTO> answers,
            List<String> excludedPetNames,
            List<Pet> similarPets
    ) {
        JSONArray answerArray = new JSONArray();
        for (AnswerItemDTO item : answers) {
            JSONObject obj = new JSONObject();
            obj.put("question", item.getQuestion());
            obj.put("answer", item.getAnswer());
            answerArray.put(obj);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("당신은 반려동물 추천 전문가입니다. 아래는 사용자의 질문과 그에 대한 응답입니다. 응답은 서술형 또는 짧은 문장입니다.\n")
                .append("이를 참고하여 사용자의 성향 점수(activity, sociability, care, emotional_bond, environment, routine)를 1~5점으로 평가하세요.\n")
                .append("💡 점수화 가이드:\n")
                .append("- 응답이 적극적일수록 높은 점수 (4~5점), 소극적이거나 모호할수록 낮은 점수 (1~2점)\n")
                .append("- 가능한 점수는 2~3점 중심 분포를 권장합니다.\n")
                .append("- 1개의 질문은 1~2개 기준에 반영되므로, 카테고리별로 나누어 평균 점수를 도출하세요.")
                .append("다음은 이전 추천에서 제외해야 할 반려동물 목록입니다:\n");

        for (String name : excludedPetNames) {
            sb.append("- ").append(name).append("\n");
        }

        if (!similarPets.isEmpty()) {
            sb.append("다음은 기존 데이터에 있는 유사 반려동물입니다. 유사한 종을 추천할 경우, trait_scores를 참고해 유사 점수를 부여하세요:\n");
            for (Pet pet : similarPets) {
                sb.append("- 이름: ").append(pet.getPetNm())
                        .append(", 종: ").append(pet.getSpecies())
                        .append(", 품종: ").append(pet.getBreed())
                        .append(", trait_scores: ").append(pet.getTraitScores())
                        .append("\n");
            }
        }

        sb.append("\n[응답 JSON]\n").append(answerArray);

        return sb.toString();
    }

    public static List<AnswerItemDTO> parseQuestionItems(String gptResponse) {
        List<AnswerItemDTO> result = new ArrayList<>();
        String clean = extractJsonObjectOnly(gptResponse);
        JSONArray arr = new JSONArray(clean);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            AnswerItemDTO item = new AnswerItemDTO();
            item.setQuestion(obj.getString("question"));
            item.setCategory(obj.getString("category"));
            result.add(item);
        }
        return result;
    }


    public static RecommendationResultDTO parseGptResponse(String gptResponse) {
        String json = extractJsonObjectOnly(gptResponse);
        JSONObject obj = new JSONObject(json);
        RecommendationResultDTO dto = new RecommendationResultDTO();

        JSONObject scores = obj.getJSONObject("user_scores");
        Map<String, Integer> userScores = new HashMap<>();
        for (String key : scores.keySet()) {
            userScores.put(key, scores.getInt(key));
        }
        dto.setUserScores(userScores);

        JSONArray recs = obj.getJSONArray("recommendations");
        List<RecommendationDTO> list = new ArrayList<>();
        for (int i = 0; i < recs.length(); i++) {
            JSONObject o = recs.getJSONObject(i);
            RecommendationDTO r = new RecommendationDTO();
            r.setRank(o.getInt("rank"));
            r.setAnimal(o.getString("animal"));
            r.setSpecies(o.getString("species"));
            r.setBreed(o.getString("breed"));
            r.setCareLevel(o.getString("care_level"));
            r.setIsSpecial(o.getString("is_special"));
            r.setTraitScores(o.getString("trait_scores"));
            r.setReason(o.getString("reason"));
            list.add(r);
        }
        dto.setRecommendations(list);
        return dto;
    }

    private static String extractJsonObjectOnly(String gptResponse) {
        int start = gptResponse.indexOf("{");
        int end = gptResponse.lastIndexOf("}");
        return (start >= 0 && end > start) ? gptResponse.substring(start, end + 1).trim() : "{}";
    }
}