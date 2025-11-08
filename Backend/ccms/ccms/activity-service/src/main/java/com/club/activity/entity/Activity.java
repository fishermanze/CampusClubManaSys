package com.club.activity.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.club.common.dto.MessageDTO;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("activity")
public class Activity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long clubId;             // 所属社团ID
    private Long founderId;          // 发布人ID（社团创始人）
    private String title;            // 活动标题
    private String content;          // 活动内容
    private LocalDateTime startTime; // 开始时间
    private LocalDateTime endTime;   // 结束时间
    private String location;         // 活动地点
    private Integer status;          // 状态：0-草稿，1-发布中，2-已结束
    private LocalDateTime createTime;// 创建时间
}