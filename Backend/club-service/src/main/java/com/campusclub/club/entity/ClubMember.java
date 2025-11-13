package com.campusclub.club.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "club_members")
public class ClubMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clubId;
    private Long userId;
    private Integer role; // 0: 普通成员, 1: 社团负责人, 2: 管理员
    private Integer status; // 0: 待审核, 1: 已加入, 2: 已退出, 3: 已开除
    private Date joinTime;
    private Date updateTime;
    private Integer activityCount;
    private Double totalScore;
    private Integer level; // 社员职级
    private Integer yearEvaluationStatus; // 0: 未评价, 1: 已评价

    // 申请信息（用于审核页面展示）
    private String realName;
    private String gender;
    private String major;
    private String className;
    @Column(length = 1000)
    private String reason;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;
}