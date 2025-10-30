package com.example.ccms.entity;

import lombok.Data;

import jakarta.persistence.*;

@Data
@Entity
@Table(name = "club_image")
public class ClubImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "club_id", nullable = false)
    private Long clubId;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "is_main")
    private Boolean isMain = false;

    private Integer sort; // 排序字段，数字越小越靠前
}