package com.campusclub.club.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "clubs")
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String logo;
    private String category;
    private Date createTime;
    private Date updateTime;
    private Integer status; // 0: 待审核, 1: 正常, 2: 已禁用
    private Long leaderId;
    private Integer memberCount;
    private String contactInfo;
    private Integer totalActivityCount;
}