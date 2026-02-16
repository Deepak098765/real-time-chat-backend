package com.deepak.chat.chat_app_backend.ai.controller;

import com.deepak.chat.chat_app_backend.ai.client.AiTestService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiTestController {
    private final AiTestService aiTestService;

    public AiTestController(AiTestService aiTestService){
        this.aiTestService = aiTestService;
    }

    @PostMapping("/test")
    public String testAi(@RequestBody String message) throws Exception  {
        return aiTestService.callModel(message);
    }
}
