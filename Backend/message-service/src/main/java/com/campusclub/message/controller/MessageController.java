package com.campusclub.message.controller;

import com.campusclub.message.dto.MessageDTO;
import com.campusclub.message.dto.ConversationDTO;
import com.campusclub.message.entity.Message;
import com.campusclub.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 发送消息
     */
    @PostMapping
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody Message message) {
        MessageDTO messageDTO = messageService.sendMessage(message);
        return ResponseEntity.ok(messageDTO);
    }

    /**
     * 获取会话消息列表
     */
    @GetMapping("/conversation")
    public ResponseEntity<List<MessageDTO>> getConversationMessages(
            @RequestParam Long userId1,
            @RequestParam Long userId2,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        List<MessageDTO> messages = messageService.getConversationMessages(userId1, userId2, page, size);
        return ResponseEntity.ok(messages);
    }

    /**
     * 获取用户的所有会话列表
     */
    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<ConversationDTO>> getUserConversations(@PathVariable Long userId) {
        List<ConversationDTO> conversations = messageService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    /**
     * 将消息标记为已读
     */
    @PutMapping("/read")
    public ResponseEntity<Void> markMessagesAsRead(
            @RequestParam Long receiverId,
            @RequestParam Long senderId) {
        messageService.markMessagesAsRead(receiverId, senderId);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取用户未读消息数量
     */
    @GetMapping("/unread-count/{userId}")
    public ResponseEntity<Long> getUnreadMessageCount(@PathVariable Long userId) {
        long count = messageService.getUnreadMessageCount(userId);
        return ResponseEntity.ok(count);
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long messageId,
            @RequestParam Long userId) {
        messageService.deleteMessage(messageId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 更新会话置顶状态
     */
    @PutMapping("/conversation/sticky")
    public ResponseEntity<Void> updateConversationStickyStatus(
            @RequestParam Long userId,
            @RequestParam Long otherUserId,
            @RequestParam Boolean isSticky) {
        messageService.updateConversationStickyStatus(userId, otherUserId, isSticky);
        return ResponseEntity.ok().build();
    }

    /**
     * 更新会话免打扰状态
     */
    @PutMapping("/conversation/mute")
    public ResponseEntity<Void> updateConversationMuteStatus(
            @RequestParam Long userId,
            @RequestParam Long otherUserId,
            @RequestParam Boolean isMuted) {
        messageService.updateConversationMuteStatus(userId, otherUserId, isMuted);
        return ResponseEntity.ok().build();
    }
}