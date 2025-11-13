package com.campusclub.ai.service;

import com.campusclub.ai.config.DeepSeekConfig;
import com.campusclub.ai.dto.ChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class AiAssistantService {

    private static final Logger logger = LoggerFactory.getLogger(AiAssistantService.class);
    private static final int CONVERSATION_HISTORY_LIMIT = 10;
    private static final long CONVERSATION_EXPIRE_HOURS = 24;

    @Autowired
    private DeepSeekConfig config;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiAssistantService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.deepseek.com")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * 发送聊天消息
     */
    public ChatResponse chat(String userMessage, String conversationId) throws Exception {
        // 如果没有conversationId，创建新的
        if (conversationId == null || conversationId.isEmpty()) {
            conversationId = UUID.randomUUID().toString();
        }

        // 获取历史对话
        List<Map<String, String>> history = getConversationHistory(conversationId);

        // 构建消息列表
        ArrayNode messages = objectMapper.createArrayNode();

        // 添加系统提示
        ObjectNode systemMessage = objectMapper.createObjectNode();
        systemMessage.put("role", "system");
        systemMessage.put("content", config.getSystemPrompt());
        messages.add(systemMessage);

        // 添加历史对话
        for (Map<String, String> msg : history) {
            ObjectNode msgNode = objectMapper.createObjectNode();
            msgNode.put("role", msg.get("role"));
            msgNode.put("content", msg.get("content"));
            messages.add(msgNode);
        }

        // 添加当前用户消息
        ObjectNode userMsg = objectMapper.createObjectNode();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        // 构建请求体
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", config.getModel());
        requestBody.set("messages", messages);
        requestBody.put("temperature", 0.7);

        logger.info("Sending request to DeepSeek API for conversation: {}", conversationId);

        try {
            // 发送请求到DeepSeek API
            String response = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + config.getKey())
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode jsonResponse = objectMapper.readTree(response);

            // 提取AI回复
            String aiMessage = jsonResponse.get("choices")
                    .get(0)
                    .get("message")
                    .get("content")
                    .asText();

            // 保存对话历史到Redis
            Map<String, String> userMsgMap = new HashMap<>();
            userMsgMap.put("role", "user");
            userMsgMap.put("content", userMessage);

            Map<String, String> aiMsgMap = new HashMap<>();
            aiMsgMap.put("role", "assistant");
            aiMsgMap.put("content", aiMessage);

            saveConversationHistory(conversationId, userMsgMap, aiMsgMap);

            logger.info("Successfully received response from DeepSeek API");

            return new ChatResponse(aiMessage, conversationId, System.currentTimeMillis());
        } catch (Exception e) {
            logger.error("Error calling DeepSeek API", e);
            throw new Exception("AI助手暂时不可用: " + e.getMessage());
        }
    }

    /**
     * 获取对话历史（从Redis）
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, String>> getConversationHistory(String conversationId) {
        String key = "ai:conversation:" + conversationId;
        List<Object> history = redisTemplate.opsForList().range(key, 0, -1);
        List<Map<String, String>> result = new ArrayList<>();

        if (history != null) {
            for (Object item : history) {
                if (item instanceof Map) {
                    result.add((Map<String, String>) item);
                }
            }
        }

        return result;
    }

    /**
     * 保存对话历史到Redis
     */
    private void saveConversationHistory(String conversationId,
                                        Map<String, String> userMsg,
                                        Map<String, String> assistantMsg) {
        String key = "ai:conversation:" + conversationId;

        // 添加用户消息
        redisTemplate.opsForList().rightPush(key, userMsg);
        // 添加AI回复
        redisTemplate.opsForList().rightPush(key, assistantMsg);

        // 限制历史记录数量
        Long size = redisTemplate.opsForList().size(key);
        if (size != null && size > CONVERSATION_HISTORY_LIMIT * 2) {
            // 删除最旧的一对对话
            redisTemplate.opsForList().trim(key, 2, -1);
        }

        // 设置过期时间
        redisTemplate.expire(key, CONVERSATION_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    /**
     * 清除对话历史
     */
    public void clearConversation(String conversationId) {
        String key = "ai:conversation:" + conversationId;
        redisTemplate.delete(key);
        logger.info("Cleared conversation: {}", conversationId);
    }
}
