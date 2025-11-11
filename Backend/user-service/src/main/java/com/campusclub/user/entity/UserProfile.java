package com.campusclub.user.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "user_profiles")
@EntityListeners(AuditingEntityListener.class)
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "student_id", length = 20)
    private String studentId;

    @Column(name = "major", length = 100)
    private String major;

    @Column(name = "grade", length = 20)
    private String grade;

    @Column(name = "class_name", length = 20)
    private String className;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "hobbies", length = 500)
    private String hobbies;

    @Column(name = "bio", length = 1000)
    private String bio;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "emergency_contact", length = 20)
    private String emergencyContact;

    @Column(name = "emergency_phone", length = 20)
    private String emergencyPhone;

    @Column(name = "social_media", length = 500)
    private String socialMedia; // JSON格式存储社交媒体信息

    @Column(name = "preferred_club_types", length = 500)
    private String preferredClubTypes; // JSON格式存储偏好的社团类型

    @Column(name = "avatar", length = 500)
    private String avatar;

    @CreatedDate
    @Column(name = "created_at", nullable = true, updatable = false)
    private Date createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = true)
    private Date updatedAt;

    /**
     * 获取用户ID
     * @return 用户ID
     */
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }
}