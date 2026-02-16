package com.deepak.chat.chat_app_backend.ai.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.Instant;

@Document("ai_token_usage")
@Data
public class AiTokenUsage {

    @Id
    private String id;

    private String endpoint;

    private int inputTokens;
    private int outputTokens;
    private int totalTokens;

    private Instant timestamp;
}

