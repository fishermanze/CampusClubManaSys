package com.campusclub.message.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notifications")
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 接收者ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 通知类型：1-系统通知 2-社团通知 3-活动通知 4-互动通知
    @Column(name = "notification_type", nullable = false)
    private Integer notificationType;

    // 通知标题
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    // 通知内容
    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    // 通知状态：0-未读 1-已读
    @Column(name = "status", nullable = false)
    private Integer status = 0;

    // 关联ID
    @Column(name = "related_id")
    private Long relatedId;

    // 关联类型
    @Column(name = "related_type")
    private String relatedType;

    // 是否需要推送
    @Column(name = "need_push", nullable = false)
    private Boolean needPush = true;

    // 创建时间
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 更新时间
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}