package com.campusclub.stats.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "stat_data")
public class StatData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String statType; // 统计类型：club_activity_ratio, member_activity_rank, monthly_score_avg
    private Long clubId;
    private Long userId;
    private String dataKey; // 统计维度键
    private Double dataValue; // 统计值
    private Date statDate;
    private String extraInfo; // 额外信息，JSON格式存储复杂数据
}