package com.campusclub.user.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "real_name", length = 100)
    private String realName;

    @Column(name = "email", length = 100, unique = true)
    private String email;

    @Column(name = "phone", length = 20, unique = true)
    private String phone;

    @Column(name = "role", length = 50, nullable = false)
    private String role; // STUDENT, TEACHER, ADMIN, CLUB_ADMIN

    @Column(name = "status", length = 20, nullable = false)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE, SUSPENDED

    @Column(name = "last_login")
    private Date lastLogin;

    @Column(name = "login_count")
    private Integer loginCount = 0;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;
}