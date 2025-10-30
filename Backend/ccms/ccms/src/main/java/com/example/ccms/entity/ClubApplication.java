package com.example.ccms.entity;

import com.example.ccms.enums.ApplicationStatusEnum;
import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "club_application")
public class ClubApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "club_id")
    private Long clubId;

    private String reason;

    @Enumerated(EnumType.STRING)
    private ApplicationStatusEnum status;

    private String feedback;

    @Column(name = "apply_time")
    private LocalDateTime applyTime;
}