package com.deepak.chat.chat_app_backend.ai.repository;


import com.deepak.chat.chat_app_backend.ai.entity.SmartReplyCache;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SmartReplyCacheRepository
        extends MongoRepository<SmartReplyCache, String> {

    Optional<SmartReplyCache> findByMessageHash(String messageHash);
}

