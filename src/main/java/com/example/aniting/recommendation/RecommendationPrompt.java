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
                "activity, sociability, care, emotional_bond, environment, routine " +
                "6개 기준을 고루 다루는 질문을 6가지 모든 기준들에 대해 충분히 판단 가능하다고 생각될 때까지 10개에서 20개 내로 작성해주세요.\n\n" +

                "각 질문은 **서술형 또는 짧은 문장으로 답할 수 있는 형태**여야 합니다. 예/아니오로 대답하는 질문은 만들지 마세요.\n" +
                "예를 들어 '밖에 자주 나가시나요?' 대신 '당신은 평소에 어떤 활동적인 취미를 즐기시나요?' 같은 식의 질문을 만들어야 합니다.\n\n" +

                "각 질문은 JSON 객체로 구성되며, 'question'과 'category' 필드를 포함해야 합니다. " +
                "각 질문은 오직 하나의 성향 기준(category)을 평가하도록 하세요.\n\n" +

                "아래 형식의 JSON 배열로만 출력하세요. 다른 설명은 출력하지 마세요:\n\n" +
                "[\n" +
                "  {\n" +
                "    \"question\": \"혼자 있을 때는 주로 어떤 활동을 하며 시간을 보내시나요?\",\n" +
                "    \"category\": \"activity\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"question\": \"일상에서 어떤 루틴을 가장 중요하게 지키고 계신가요?\",\n" +
                "    \"category\": \"routine\"\n" +
                "  }\n" +
                "]";
    }

    public static String buildRecommendationPrompt(List<AnswerItemDTO> answers, List<String> previousPetNames, List<Pet> similarPetsFromDB) {
        JSONArray arr = new JSONArray();
        for (AnswerItemDTO item : answers) {
            JSONObject obj = new JSONObject();
            obj.put("question", item.getQuestion());
            obj.put("answer", item.getAnswer());
            arr.put(obj);
        }

        StringBuilder prompt = new StringBuilder();

        prompt.append("당신은 반려동물 추천 전문가입니다. 아래는 사용자의 질문과 그에 대한 응답입니다. 응답은 서술형 또는 짧은 문장입니다. 이를 참고하여 사용자의 성향 점수(activity, sociability, care, emotional_bond, environment, routine)를 1~5점으로 평가하세요.\n");

        prompt.append("\n💡 점수화 가이드:\n")
                .append("- 응답이 적극적일수록 높은 점수 (4~5점), 소극적이거나 모호할수록 낮은 점수 (1~2점)\n")
                .append("- 가능한 점수는 2~3점 중심 분포를 권장합니다.\n")
                .append("- 1개의 질문은 1~2개 기준에 반영되므로, 카테고리별로 나누어 평균 점수를 도출하세요.\n\n");

        if (previousPetNames != null && !previousPetNames.isEmpty()) {
            prompt.append("다음 반려동물은 이미 추천된 적이 있으므로, 이번 추천에서는 제외해 주세요:\n");
            for (String name : previousPetNames) {
                prompt.append("- ").append(name).append("\n");
            }
            prompt.append("\n");
        }

        if (similarPetsFromDB != null && !similarPetsFromDB.isEmpty()) {
            prompt.append("다음은 유사한 종(species) 또는 품종(breed)을 가진 기존 반려동물입니다. ")
                    .append("이들과 유사한 동물을 추천하는 경우, 아래 trait_scores를 참고하여 비슷한 성향 점수를 적용해 주세요:\n");
            for (Pet pet : similarPetsFromDB) {
                prompt.append("- 이름: ").append(pet.getPetNm())
                        .append(", 종: ").append(pet.getSpecies())
                        .append(", 품종: ").append(pet.getBreed())
                        .append(", trait_scores: ").append(pet.getTraitScores()).append("\n");
            }
            prompt.append("\n");
        }

        prompt.append("점수 평가 후, 사용자의 성향에 맞는 반려동물 3가지를 추천해주세요. 아래 조건을 반드시 따르세요:\n")
                .append("1. 추천 동물은 일반적인 동물(예: 강아지, 고양이) 외에도 이국적이거나 특이한 동물이 하나 이상 포함되도록 하세요.\n")
                .append("2. 파충류 등 일반인이 혐오감을 느낄 수 있는 동물은 제외합니다.\n")
                .append("3. 사람과 정서적 교감이 가능하고 실내 사육이 가능한 동물로 한정합니다.\n\n")

                .append("각 추천 동물에 대해 다음 정보를 반드시 구조화해 주세요:\n")
                .append("- animal: **풀네임**, 예: \"네덜란드 드워프 토끼\"\n")
                .append("- species: **쉽게 정의할 수 있는 일반 동물명**, 예: \"토끼\", \"강아지\"\n")
                .append("- breed: **그 동물의 세부 품종 이름**, 예: \"드워프 토끼\"\n")
                .append("- care_level: 돌봄 난이도 (낮음, 중간, 높음)\n")
                .append("- is_special: 특이 품종 여부 (\"Y\" 또는 \"N\")\n")
                .append("- trait_scores: 이 동물의 6가지 성향 점수를 콤마(,)로 구분된 문자열로 제공해주세요. 순서는 activity, sociability, care, emotional_bond, environment, routine입니다. 예: \"3,2,4,5,2,3\"\n")
                .append("- reason: 사용자 성향과 이 동물이 잘 맞는 이유\n\n")

                .append("💡 예시 형식:\n")
                .append("{\n")
                .append("  \"user_scores\": {\n")
                .append("    \"activity\": 3,\n")
                .append("    \"sociability\": 2,\n")
                .append("    \"care\": 4,\n")
                .append("    \"emotional_bond\": 3,\n")
                .append("    \"environment\": 3,\n")
                .append("    \"routine\": 2\n")
                .append("  },\n")
                .append("  \"recommendations\": [\n")
                .append("    {\n")
                .append("      \"rank\": 1,\n")
                .append("      \"animal\": \"네덜란드 드워프 토끼\",\n")
                .append("      \"species\": \"토끼\",\n")
                .append("      \"breed\": \"드워프 토끼\",\n")
                .append("      \"care_level\": \"중간\",\n")
                .append("      \"is_special\": \"Y\",\n")
                .append("      \"trait_scores\": \"2,2,4,3,4,3\",\n")
                .append("      \"reason\": \"낮은 활동성과 규칙적인 루틴을 가진 사용자에게 적합합니다.\"\n")
                .append("    },\n")
                .append("    { ... },\n")
                .append("    { ... }\n")
                .append("  ]\n")
                .append("}\n\n")
                .append("네덜란드 드워프 토끼는 구조화를 위한 예시일 뿐 추천하는 데에는 영향을 주지 마세요.")
                .append(arr.toString());

        return prompt.toString();
    }

    // 🔵 3. 질문 목록 파싱 (JSONArray 기반)
    public static List<String> parseQuestionList(String gptResponse) {
        String clean = extractJsonArrayOnly(gptResponse);
        JSONArray array = new JSONArray(clean);
        List<String> questions = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String question = obj.getString("question");

            questions.add(question);
        }

        return questions;
    }

    public static List<AnswerItemDTO> parseQuestionItems(String gptResponse) {
        String clean = extractJsonArrayOnly(gptResponse);
        JSONArray array = new JSONArray(clean);
        List<AnswerItemDTO> items = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            AnswerItemDTO item = new AnswerItemDTO();
            item.setQuestion(obj.getString("question"));
            item.setCategory(obj.getString("category"));
            items.add(item);
        }

        return items;
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
            r.setTraitScores(rec.optString("trait_scores", ""));
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
