package com.campusclub.message.service;

import com.campusclub.message.dto.MessageDTO;
import com.campusclub.message.dto.ConversationDTO;
import com.campusclub.message.entity.Message;

import java.util.List;

public interface MessageService {

    /**
     * 发送消息
     */
    MessageDTO sendMessage(Message message);

    /**
     * 获取会话消息列表
     */
    List<MessageDTO> getConversationMessages(Long userId1, Long userId2, int page, int size);

    /**
     * 获取用户的所有会话列表
     */
    List<ConversationDTO> getUserConversations(Long userId);

    /**
     * 将消息标记为已读
     */
    void markMessagesAsRead(Long receiverId, Long senderId);

    /**
     * 获取用户未读消息数量
     */
    long getUnreadMessageCount(Long userId);

    /**
     * 删除消息（软删除）
     */
    void deleteMessage(Long messageId, Long userId);

    /**
     * 更新会话置顶状态
     */
    void updateConversationStickyStatus(Long userId, Long otherUserId, Boolean isSticky);

    /**
     * 更新会话免打扰状态
     */
    void updateConversationMuteStatus(Long userId, Long otherUserId, Boolean isMuted);
}