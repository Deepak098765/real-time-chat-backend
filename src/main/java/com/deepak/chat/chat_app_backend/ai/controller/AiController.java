package com.deepak.chat.chat_app_backend.ai.controller;

import com.deepak.chat.chat_app_backend.ai.dto.AiRequest;
import com.deepak.chat.chat_app_backend.ai.dto.AiResponse;
import com.deepak.chat.chat_app_backend.ai.client.GeminiClient;
import org.springframework.web.bind.annotation.*;
import com.deepak.chat.chat_app_backend.ai.client.GroqClient;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AiController {

//    private final OpenAiService openAiService;
//
//    public AiController(OpenAiService openAiService) {
//        this.openAiService = openAiService;
//    }
//
//    @PostMapping("/smart-reply")
//    public AiResponse getSmartReply(@RequestBody AiRequest request) throws Exception {
//
//        String aiRawResponse = openAiService.getSmartReply(request.getMessage());
//
//        AiResponse response = new AiResponse();
//        response.setReply(aiRawResponse);
//
//        return response;
//    }

//    private final GeminiClient geminiClient;
//
//    public AiController(GeminiClient geminiClient) {
//        this.geminiClient = geminiClient;
//    }
//
//    @PostMapping("/smart-reply")
//    public AiResponse getSmartReply(@RequestBody AiRequest request) throws Exception {
//
//        List<String> replies = geminiClient.getSmartReply(request.getMessage());
////        AiResponse response = new AiResponse();
////        response.setReply(result);
//
//        return new AiResponse(replies);
//    }

    private final GroqClient groqClient;

    public AiController(GroqClient groqClient) {
        this.groqClient = groqClient;
    }

    @PostMapping("/smart-reply")
    public AiResponse getSmartReply(@RequestBody AiRequest request) throws Exception {

        List<String> replies = groqClient.getSmartReply(request.getMessage());

        return new AiResponse(replies);
    }
}

