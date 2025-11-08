package com.club.common.dto;

import lombok.Data;

/**
 * 跨模块通用的消息DTO（用于社团服务向消息服务发送通知）
 */
@Data
public class MessageDTO {
    /**
     * 接收人ID（用户ID）
     */
    private Long receiverId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型
     * 1-社团申请通知（发给创始人）
     * 2-审批结果通知（发给申请人）
     */
    private Integer type;
}