package com.campusclub.auth.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
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

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String realName;

    @Column(nullable = false)
    private String role; // member, club_manager, admin

    private String avatar;

    private String email;

    private String phone;

    private String department;

    private String grade;

    private String hobbies;

    @Column(nullable = false)
    private boolean enabled = true;

    // Ensure proper getter naming for boolean property
    public boolean isEnabled() {
        return enabled;
    }

    @CreatedDate
    @Column(updatable = false)
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;
}