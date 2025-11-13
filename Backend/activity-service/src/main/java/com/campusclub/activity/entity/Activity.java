package com.campusclub.activity.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "activities")
@EntityListeners(AuditingEntityListener.class)
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 活动名称
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    // 活动描述
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // 活动内容（富文本）
    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    // 活动封面图
    @Column(name = "cover_image", length = 500)
    private String coverImage;

    // 活动图片列表（JSON格式存储）
    @Column(name = "images", columnDefinition = "TEXT")
    private String images;

    // 活动开始时间
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    // 活动结束时间
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    // 活动地点
    @Column(name = "location", nullable = false, length = 500)
    private String location;

    // 活动负责人ID
    @Column(name = "organizer_id", nullable = false)
    private Long organizerId;

    // 社团ID
    @Column(name = "club_id", nullable = false)
    private Long clubId;

    // 活动状态：0-草稿 1-已发布 2-进行中 3-已完成 4-已取消
    @Column(name = "status", nullable = false)
    private Integer status = 0;

    // 最大参与人数
    @Column(name = "max_participants")
    private Integer maxParticipants;

    // 当前参与人数
    @Column(name = "current_participants")
    private Integer currentParticipants = 0;

    // 报名截止时间
    @Column(name = "enrollment_deadline")
    private LocalDateTime enrollmentDeadline;

    // 活动标签（JSON格式存储）
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;

    // 是否需要审核
    @Column(name = "need_approval", nullable = false)
    private Boolean needApproval = false;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    // 创建时间
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 更新时间
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 活动参与记录
    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private List<ActivityParticipant> participants;

    // 活动评论
    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private List<ActivityComment> comments;
}