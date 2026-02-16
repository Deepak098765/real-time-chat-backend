package com.deepak.chat.chat_app_backend.ai.service;


import com.deepak.chat.chat_app_backend.ai.client.GeminiClient;
import com.deepak.chat.chat_app_backend.ai.client.GroqClient;
import com.deepak.chat.chat_app_backend.ai.entity.SmartReplyCache;
import com.deepak.chat.chat_app_backend.ai.repository.SmartReplyCacheRepository;
import com.deepak.chat.chat_app_backend.ai.util.HashUtil;
import io.github.bucket4j.Bucket;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class AsyncSmartReplyService {

    private final GroqClient groqClient;
    //private final GeminiClient geminiClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final SmartReplyCacheRepository cacheRepository;
    private final RateLimitService rateLimitService;



//    public AsyncSmartReplyService(GeminiClient geminiClient,
//                                  SimpMessagingTemplate messagingTemplate) {
//        this.geminiClient = geminiClient;
//        this.messagingTemplate = messagingTemplate;
//    }

//    public AsyncSmartReplyService(
//            GeminiClient geminiClient,
//            SimpMessagingTemplate messagingTemplate,
//            SmartReplyCacheRepository cacheRepository
//    ) {
//        this.geminiClient = geminiClient;
//        this.messagingTemplate = messagingTemplate;
//        this.cacheRepository = cacheRepository;
//    }

    public AsyncSmartReplyService(
            GroqClient groqClient,
            SimpMessagingTemplate messagingTemplate,
            SmartReplyCacheRepository cacheRepository,
            RateLimitService rateLimitService
    ) {
        this.groqClient = groqClient;
        this.messagingTemplate = messagingTemplate;
        this.cacheRepository = cacheRepository;
        this.rateLimitService = rateLimitService;
    }



//    @Async
//    public void generateSmartReplyAsync(String message, String roomId) {
//
//        try {
//            List<String> replies = geminiClient.getSmartReply(message);
//
//            messagingTemplate.convertAndSend(
//                    "/topic/smart-reply/" + roomId,
//                    replies
//            );
//
//        } catch (Exception e) {
//            System.out.println("Async Smart Reply Failed: " + e.getMessage());
//        }
//    }

    @Async
    public void generateSmartReplyAsync(String message, String roomId) {

        try {

            Bucket bucket = rateLimitService.resolveBucket(roomId);

            if (!bucket.tryConsume(1)) {

                System.out.println("Rate limit exceeded");

                messagingTemplate.convertAndSend(
                        "/topic/smart-reply/" + roomId,
                        List.of("You're sending messages too fast")
                );

                return;
            }
            String hash = HashUtil.hashMessage(message);

            // ✅ Check Cache First
            Optional<SmartReplyCache> cached =
                    cacheRepository.findByMessageHash(hash);

            List<String> replies;

            if (cached.isPresent()) {

                replies = cached.get().getReplies();
                System.out.println("Cache HIT");

            } else {

                System.out.println("Cache MISS → Calling Groq");

                replies = groqClient.getSmartReply(message);

                SmartReplyCache cache = new SmartReplyCache();
                cache.setMessageHash(hash);
                cache.setReplies(replies);
                cache.setCreatedAt(Instant.now());

                cacheRepository.save(cache);
            }

            messagingTemplate.convertAndSend(
                    "/topic/smart-reply/" + roomId,
                    replies
            );

        } catch (Exception e) {
            System.out.println("Smart Reply Error: " + e.getMessage());
        }
    }

}

