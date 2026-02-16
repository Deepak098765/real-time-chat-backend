package com.deepak.chat.chat_app_backend.ai.client;

import com.deepak.chat.chat_app_backend.ai.service.AiTokenUsageService;
import com.deepak.chat.chat_app_backend.ai.util.TokenEstimator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GeminiClient {

    @Value("${gemini.api.key}")
    private String apiKey;

    //private final OkHttpClient client = new OkHttpClient();
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build();

    private final AiTokenUsageService tokenUsageService;

    public GeminiClient(AiTokenUsageService tokenUsageService) {
        this.tokenUsageService = tokenUsageService;
    }
    public List<String> getSmartReply(String userMessage) throws Exception {

        if(userMessage == null || userMessage.trim().isEmpty()) {
            return List.of("Okay", "Sure", "Alright");
        }


        String json = """
{
  "contents": [
    {
      "role": "user",
      "parts": [
        {
          "text": "You are a chat assistant that returns short natural replies suitable for real-time messaging. Respond with exactly 3 short replies only. No explanations, no quotes, no numbering."
        }
      ]
    },
    {
      "role": "user",
      "parts": [
        {
          "text": "Generate 3 reply suggestions for: %s"
        }
      ]
    }
  ]
}
""".formatted(userMessage);


        Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey)
                .post(RequestBody.create(json, MediaType.parse("application/json")))
                .build();

//        try (Response response = client.newCall(request).execute()) {
//
//            if (!response.isSuccessful()) {
//                return List.of("Okay", "Got it", "Sounds good");
//            }
//
//            String body = response.body().string();
//            JsonNode root = new ObjectMapper().readTree(body);
//
//            JsonNode candidates = root.path("candidates");
//
//            if (!candidates.isArray() || candidates.isEmpty()) {
//                return List.of("Okay", "Sure", "Alright");
//            }
//
//            JsonNode parts = candidates
//                    .get(0)
//                    .path("content")
//                    .path("parts");
//
//            if (!parts.isArray() || parts.isEmpty()) {
//                return List.of("Okay", "Sure", "Alright");
//            }
//
//            String text = parts.get(0).path("text").asText("");
//
//            if (text.isBlank()) {
//                return List.of("Okay", "Sure", "Alright");
//            }
//
//            List<String> replies = Arrays.stream(text.split("\\n"))
//                    .map(s -> s.replaceAll("^\\d+\\.\\s*", ""))
//                    .map(s -> s.replaceAll("\"", ""))
//                    .map(s -> s.replaceAll("\\(.*?\\)", ""))
//                    .map(String::trim)
//                    .filter(s -> !s.isEmpty())
//                    .limit(3)
//                    .toList();
//
//            return replies.isEmpty()
//                    ? List.of("Okay", "Sure", "Alright")
//                    : replies;
//        }
        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                return List.of("Okay", "Got it", "Sounds good");
            }

            String body = response.body().string();
            JsonNode root = new ObjectMapper().readTree(body);

            JsonNode candidates = root.path("candidates");

            if (!candidates.isArray() || candidates.isEmpty()) {
                return List.of("Okay", "Sure", "Alright");
            }

            JsonNode parts = candidates
                    .get(0)
                    .path("content")
                    .path("parts");

            if (!parts.isArray() || parts.isEmpty()) {
                return List.of("Okay", "Sure", "Alright");
            }

            String text = parts.get(0).path("text").asText("");

            if (text.isBlank()) {
                return List.of("Okay", "Sure", "Alright");
            }

//            List<String> replies = Arrays.stream(text.split("\\n"))
//                    .map(s -> s.replaceAll("^\\d+\\.\\s*", ""))
//                    .map(s -> s.replaceAll("\"", ""))
//                    .map(s -> s.replaceAll("\\(.*?\\)", ""))
//                    .map(String::trim)
//                    .filter(s -> !s.isEmpty())
//                    .limit(3)
//                    .toList();
//
//            return replies.isEmpty()
//                    ? List.of("Okay", "Sure", "Alright")
//                    : replies;
            List<String> replies = Arrays.stream(text.split("\\n"))
                    .map(s -> s.replaceAll("^\\d+\\.\\s*", ""))
                    .map(s -> s.replaceAll("\"", ""))
                    .map(s -> s.replaceAll("\\(.*?\\)", ""))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .limit(3)
                    .toList();

// ❌ If replies empty → fallback → DO NOT LOG TOKENS
            if (replies.isEmpty()) {
                return List.of("Okay", "Sure", "Alright");
            }

// ✅ TOKEN LOGGING ONLY FOR REAL AI RESPONSE
            int inputTokens = TokenEstimator.estimateTokens(userMessage);
            int outputTokens = TokenEstimator.estimateTokens(text);

            tokenUsageService.logUsage(
                    "smart-reply",
                    inputTokens,
                    outputTokens
            );

// ✅ FINAL RETURN
            return replies;


        } catch (java.net.SocketTimeoutException e) {
            System.out.println("Gemini Timeout: " + e.getMessage());
            return List.of("Okay", "Got it", "Sounds good");

        } catch (Exception e) {
            System.out.println("Gemini Error: " + e.getMessage());
            return List.of("Okay", "Sure", "Alright");
        }

    }

}

