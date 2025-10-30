package com.example.ccms.service;

import com.example.ccms.dto.response.ApiResponse;
import com.example.ccms.dto.response.MessageVO;
import com.example.ccms.enums.MessageTypeEnum;

import java.util.List;

public interface MessageService {
    /**
     * 查询用户消息列表（分页）
     * @param userId 用户ID
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页条数
     * @return 消息列表
     */
    ApiResponse<List<MessageVO>> getUserMessages(Long userId, int pageNum, int pageSize);

    /**
     * 查询用户未读消息数量
     * @param userId 用户ID
     * @return 未读数量
     */
    ApiResponse<Long> getUnreadCount(Long userId);

    /**
     * 标记单条消息为已读
     * @param messageId 消息ID
     * @param userId 用户ID（校验消息归属）
     * @return 操作结果
     */
    ApiResponse<Void> markAsRead(Long messageId, Long userId);

    /**
     * 标记所有消息为已读
     * @param userId 用户ID
     * @return 操作结果
     */
    ApiResponse<Void> markAllAsRead(Long userId);

    /**
     * 发送消息（内部调用，用于系统通知、申请通知等）
     * @param userId 接收消息的用户ID
     * @param clubId 关联的社团ID（可为null）
     * @param content 消息内容
     * @param type 消息类型
     */
    void sendMessage(Long userId, Long clubId, String content, MessageTypeEnum type);
}