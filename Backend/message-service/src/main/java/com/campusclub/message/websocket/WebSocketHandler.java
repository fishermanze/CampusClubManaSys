package com.campusclub.message.websocket;

import com.campusclub.message.dto.MessageDTO;
import com.campusclub.message.dto.NotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@Slf4j
public class WebSocketHandler {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 处理点对点消息发送
     */
    @MessageMapping("/chat.private")
    public void handlePrivateMessage(@Payload MessageDTO message, SimpMessageHeaderAccessor headerAccessor) {
        log.info("收到点对点消息: {} 发送给 {}", message.getSenderId(), message.getReceiverId());
        
        // 将消息发送给特定用户
        messagingTemplate.convertAndSendToUser(
                message.getReceiverId().toString(),
                "/queue/messages",
                message
        );
    }

    /**
     * 处理群聊消息
     */
    @MessageMapping("/chat.group")
    @SendTo("/topic/group")
    public MessageDTO handleGroupMessage(@Payload MessageDTO message) {
        log.info("收到群聊消息: {}", message.getContent());
        return message;
    }

    /**
     * 发送通知给特定用户
     */
    public void sendNotificationToUser(Long userId, NotificationDTO notification) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                notification
        );
    }

    /**
     * 广播系统通知
     */
    public void broadcastSystemNotification(NotificationDTO notification) {
        messagingTemplate.convertAndSend("/topic/system", notification);
    }

    /**
     * 用户连接处理
     */
    @MessageMapping("/user.connect")
    public void handleUserConnect(@Payload Long userId, SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        log.info("用户连接: {}", userId);
        // 这里可以保存用户会话信息
        headerAccessor.getSessionAttributes().put("userId", userId);
    }

    /**
     * 用户断开连接处理
     */
    @MessageMapping("/user.disconnect")
    public void handleUserDisconnect(@Payload Long userId) {
        log.info("用户断开连接: {}", userId);
        // 这里可以清理用户会话信息
    }
}