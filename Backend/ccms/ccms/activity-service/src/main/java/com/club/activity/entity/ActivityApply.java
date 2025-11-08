package com.club.activity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("activity_apply")
public class ActivityApply {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long activityId;         // 活动ID
    private Long applicantId;        // 申请人ID
    private Long founderId;          // 审批人ID（社团创始人）
    private Integer status;          // 状态：0-待审批，1-同意，2-拒绝
    private LocalDateTime applyTime; // 申请时间
    private LocalDateTime handleTime;// 处理时间
}