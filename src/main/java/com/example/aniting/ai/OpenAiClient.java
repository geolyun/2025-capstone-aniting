package com.example.aniting.ai;

import org.springframework.beans.factory.annotation.Value;
import org.json.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;

@Component
public class OpenAiClient {

    @Value("${openai.api.key}")
    private String apiKey;

    public String callGPTAPI(String prompt) {
        try {
            URL url = new URL("https://api.openai.com/v1/chat/completions");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-4o");
            requestBody.put("messages", new JSONArray().put(new JSONObject()
                    .put("role", "user")
                    .put("content", prompt)));
            requestBody.put("max_tokens", 1000);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            // 🔽 GPT 응답에서 "content"만 추출 (extractGPTContent 역할)
            JSONObject jsonResponse = new JSONObject(response.toString());
            String content = jsonResponse.getJSONArray( "choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();

            // 불필요한 백틱 제거
            if (content.startsWith("```")) {
                content = content.replaceAll("```json", "").replaceAll("```", "").trim();
            }

            return content;

        } catch (Exception e) {
            throw new RuntimeException("GPT 호출 중 오류 발생: " + e.getMessage());
        }
    }
}
