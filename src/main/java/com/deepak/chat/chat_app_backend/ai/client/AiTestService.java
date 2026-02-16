package com.deepak.chat.chat_app_backend.ai.client;



import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiTestService {

    @Value("${gemini.api.key}")
    private String apiKey;
    private final OkHttpClient client = new OkHttpClient();

    public String callModel(String prompt) throws Exception {
        String json = """
{
  "contents": [
    {
      "role": "user",
      "parts": [
        {
          "text": "You are a chat assistant that explain the given topic."
        }
      ]
    },
    {
      "role": "user",
      "parts": [
        {
          "text": "Answer for: %s"
        }
      ]
    }
  ]
}
""".formatted(prompt);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                json
        );

        Request request = new Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent")
                .addHeader("x-goog-api-key", apiKey)
                .post(body)
                .build();

        try(Response response = client.newCall(request).execute()){
            return response.body().string();
        }
    };

}
