package com.club.club.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("club_apply")
public class ClubApply {
    @TableId(type = IdType.AUTO)
    private Long id; // 申请ID
    private Long clubId; // 社团ID
    private Long applicantId; // 申请人ID
    private Long founderId; // 审批人（社团创始人ID）
    private Integer status; // 状态：0-待审批，1-同意，2-拒绝
    private LocalDateTime applyTime; // 申请时间
    private LocalDateTime handleTime; // 处理时间
}