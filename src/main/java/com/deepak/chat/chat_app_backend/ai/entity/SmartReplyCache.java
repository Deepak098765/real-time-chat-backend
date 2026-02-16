package com.deepak.chat.chat_app_backend.ai.entity;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.Instant;
import java.util.List;

@Document("smart_reply_cache")
@Data
public class SmartReplyCache {
    @Id
    private String id;

    private String messageHash;

    private List<String> replies;

    @Indexed(expireAfter = "24h")
    private Instant createdAt;
}
