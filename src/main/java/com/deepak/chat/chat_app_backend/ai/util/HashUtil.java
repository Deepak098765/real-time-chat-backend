package com.deepak.chat.chat_app_backend.ai.util;

public class HashUtil {
    public static String hashMessage(String message) {
        return Integer.toHexString(message.trim().toLowerCase().hashCode());
    }
}
