package com.example.ccms.entity;

import com.example.ccms.enums.RecruitStatusEnum;
import lombok.Data;

import jakarta.persistence.*;

@Data // Lombok 自动生成 getter/setter
@Entity
@Table(name = "club")
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String direction;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    // 关键字段：确保命名规范（小驼峰）
    @Column(name = "max_number") // 数据库字段名（下划线）
    private Integer maxNumber; // 实体类字段名（小驼峰）

    @Enumerated(EnumType.STRING)
    @Column(name = "recruit_status")
    private RecruitStatusEnum recruitStatus;

    @Column(name = "creator_id")
    private Long creatorId;
}