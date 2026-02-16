package com.deepak.chat.chat_app_backend.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AiResponse {
    private List<String> suggestions;
    //private String reply;
}
