package com.deepak.chat.chat_app_backend.ai.repository;

import com.deepak.chat.chat_app_backend.ai.entity.AiTokenUsage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AiTokenUsageRepository
        extends MongoRepository<AiTokenUsage, String> {
}

