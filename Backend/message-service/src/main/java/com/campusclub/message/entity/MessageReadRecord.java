package com.campusclub.message.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "message_read_records")
@EntityListeners(AuditingEntityListener.class)
public class MessageReadRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 用户ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 会话对方用户ID
    @Column(name = "other_user_id", nullable = false)
    private Long otherUserId;

    // 最后阅读的消息ID
    @Column(name = "last_read_message_id")
    private Long lastReadMessageId;

    // 最后阅读时间
    @Column(name = "last_read_time")
    private LocalDateTime lastReadTime;

    // 未读消息数量
    @Column(name = "unread_count", nullable = false)
    private Integer unreadCount = 0;

    // 是否置顶
    @Column(name = "is_sticky", nullable = false)
    private Boolean isSticky = false;

    // 是否免打扰
    @Column(name = "is_muted", nullable = false)
    private Boolean isMuted = false;

    // 创建时间
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 更新时间
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}