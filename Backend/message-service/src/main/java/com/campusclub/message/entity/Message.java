package com.campusclub.message.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "messages")
@EntityListeners(AuditingEntityListener.class)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 发送者ID
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    // 接收者ID
    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    // 消息类型：1-聊天消息 2-通知消息
    @Column(name = "message_type", nullable = false)
    private Integer messageType;

    // 消息内容
    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    // 消息状态：0-未读 1-已读 2-已删除
    @Column(name = "status", nullable = false)
    private Integer status = 0;

    // 关联ID（例如：帖子ID、评论ID等）
    @Column(name = "related_id")
    private Long relatedId;

    // 关联类型（例如：帖子、评论等）
    @Column(name = "related_type")
    private String relatedType;

    // 创建时间
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 更新时间
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}