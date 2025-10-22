package com.example.ccms.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Column;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(unique = true, nullable = false)
    private String uid; // 校园统一标识（学号/工号）

    @Column(unique = true, nullable = false)
    private String username; // 登录用户名

    @Column(nullable = false)
    private String password; // BCrypt加密密码

    @Column(nullable = false)
    private String realName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.level3; // 权限等级

    private Integer status = 1; // 1-正常，0-禁用

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // 权限枚举
    public enum Role {
        level1, level2, level3
    }
}