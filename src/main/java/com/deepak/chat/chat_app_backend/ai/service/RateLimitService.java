package com.deepak.chat.chat_app_backend.ai.service;


import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String userId) {

        return cache.computeIfAbsent(userId, this::newBucket);
    }

    private Bucket newBucket(String userId) {

        Bandwidth limit = Bandwidth.simple(10, Duration.ofMinutes(1));

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
