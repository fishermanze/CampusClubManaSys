package com.campusclub.activity.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "activity_participants")
@EntityListeners(AuditingEntityListener.class)
public class ActivityParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 活动ID
    @Column(name = "activity_id", nullable = false)
    private Long activityId;

    // 用户ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 报名状态：0-待审核 1-已通过 2-已拒绝 3-已参加 4-未参加
    @Column(name = "status", nullable = false)
    private Integer status = 0;

    // 报名时间（手动设置，不使用@CreatedDate避免与createdAt冲突）
    @Column(name = "enrollment_time", nullable = false, updatable = false)
    private LocalDateTime enrollmentTime;

    // 审核时间
    @Column(name = "approval_time")
    private LocalDateTime approvalTime;

    // 审核人ID
    @Column(name = "approved_by")
    private Long approvedBy;

    // 审核备注
    @Column(name = "approval_remark", length = 500)
    private String approvalRemark;

    // 签到时间
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    // 退签时间
    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    // 报名信息（额外的参与信息）
    @Column(name = "enrollment_info", length = 1000)
    private String enrollmentInfo;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    // 创建时间
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 更新时间
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 关联到活动
    @ManyToOne
    @JoinColumn(name = "activity_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Activity activity;
}