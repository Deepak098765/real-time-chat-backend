
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
import java.util.concurrent.TimeUnit;

@Service
public class GroqClient {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.model}")
    private String model;


    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    private final AiTokenUsageService tokenUsageService;

    public GroqClient(AiTokenUsageService tokenUsageService) {
        this.tokenUsageService = tokenUsageService;
    }

    public List<String> getSmartReply(String userMessage) throws Exception {

        if (userMessage == null || userMessage.trim().isEmpty()) {
            return List.of("Okay", "Sure", "Alright");
        }

        String json = """
        {
          "model": "%s",
          "messages": [
            {
              "role": "system",
              "content": "You are a chat assistant that returns short natural replies suitable for real-time messaging. Respond with exactly 3 short replies only. No explanations, no quotes, no numbering."
            },
            {
              "role": "user",
              "content": "Generate 3 reply suggestions for: %s"
            }
          ],
          "temperature": 0.7
        }
        """.formatted(model, userMessage);

        Request request = new Request.Builder()
                .url("https://api.groq.com/openai/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(json, MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {

//
            if (!response.isSuccessful()) {
                return List.of("Okay", "Got it", "Sounds good");
            }


            String body = response.body().string();
            JsonNode root = new ObjectMapper().readTree(body);

            JsonNode choices = root.path("choices");

            if (!choices.isArray() || choices.isEmpty()) {
                return List.of("Okay", "Sure", "Alright");
            }

            String text = choices
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText("");

            if (text.isBlank()) {
                return List.of("Okay", "Sure", "Alright");
            }

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
            System.out.println("Groq Timeout: " + e.getMessage());
            return List.of("Okay", "Got it", "Sounds good");

        } catch (Exception e) {
            System.out.println("Groq Error: " + e.getMessage());
            return List.of("Okay", "Sure", "Alright");
        }
    }
}

