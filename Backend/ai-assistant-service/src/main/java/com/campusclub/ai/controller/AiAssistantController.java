package com.campusclub.ai.controller;

import com.campusclub.ai.dto.ApiResponse;
import com.campusclub.ai.dto.ChatRequest;
import com.campusclub.ai.dto.ChatResponse;
import com.campusclub.ai.service.AiAssistantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AiAssistantController {

    private static final Logger logger = LoggerFactory.getLogger(AiAssistantController.class);

    @Autowired
    private AiAssistantService aiAssistantService;

    /**
     * 聊天接口
     */
    @PostMapping("/chat")
    public ApiResponse<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            logger.info("Received chat request: {}", request.getMessage());

            if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
                return ApiResponse.error(400, "消息不能为空");
            }

            ChatResponse response = aiAssistantService.chat(
                    request.getMessage(),
                    request.getConversationId()
            );

            return ApiResponse.success(response);
        } catch (Exception e) {
            logger.error("Chat request failed", e);
            return ApiResponse.error(500, "AI助手暂时不可用，请稍后再试: " + e.getMessage());
        }
    }

    /**
     * 清除对话历史
     */
    @DeleteMapping("/conversation/{conversationId}")
    public ApiResponse<String> clearConversation(@PathVariable String conversationId) {
        try {
            aiAssistantService.clearConversation(conversationId);
            return ApiResponse.success("对话历史已清除");
        } catch (Exception e) {
            logger.error("Clear conversation failed", e);
            return ApiResponse.error(500, "清除对话历史失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("AI Assistant Service is running");
    }
}

