package com.deepak.chat.chat_app_backend.ai.util;

public class TokenEstimator {

    public static int estimateTokens(String text) {

        if (text == null || text.isBlank()) return 0;

        return text.length() / 4;
    }
}

