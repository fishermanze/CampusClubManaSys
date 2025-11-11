package com.campusclub.user.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "user_follows", uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followed_id"}))
@EntityListeners(AuditingEntityListener.class)
public class UserFollow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne
    @JoinColumn(name = "followed_id", nullable = false)
    private User followed;

    @Column(name = "is_mutual")
    private Boolean isMutual = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;
}