package com.campusclub.ai.dto;

import lombok.Data;

@Data
public class ChatRequest {
    private String message;
    private String conversationId; // 可选，用于维持对话上下文
}
