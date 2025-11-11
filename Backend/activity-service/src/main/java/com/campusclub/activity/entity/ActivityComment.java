package com.campusclub.activity.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "activity_comments")
@EntityListeners(AuditingEntityListener.class)
public class ActivityComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 活动ID
    @Column(name = "activity_id", nullable = false)
    private Long activityId;

    // 用户ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 评论内容
    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    // 父评论ID（用于回复功能）
    @Column(name = "parent_id")
    private Long parentId;

    // 点赞数
    @Column(name = "likes_count")
    private Integer likesCount = 0;

    // 评论状态：0-正常 1-已删除 2-已禁用
    @Column(name = "status", nullable = false)
    private Integer status = 0;

    // 创建时间
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 更新时间
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 关联到活动
    @ManyToOne
    @JoinColumn(name = "activity_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Activity activity;
}