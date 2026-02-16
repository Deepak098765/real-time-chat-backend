package com.deepak.chat.chat_app_backend.ai.service;

import com.deepak.chat.chat_app_backend.ai.entity.AiTokenUsage;
import com.deepak.chat.chat_app_backend.ai.repository.AiTokenUsageRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class AiTokenUsageService {

    private final AiTokenUsageRepository repository;

    public AiTokenUsageService(AiTokenUsageRepository repository) {
        this.repository = repository;
    }

    public void logUsage(
            String endpoint,
            int inputTokens,
            int outputTokens
    ) {

        AiTokenUsage usage = new AiTokenUsage();
        usage.setEndpoint(endpoint);
        usage.setInputTokens(inputTokens);
        usage.setOutputTokens(outputTokens);
        usage.setTotalTokens(inputTokens + outputTokens);
        usage.setTimestamp(Instant.now());

        repository.save(usage);
    }
}

