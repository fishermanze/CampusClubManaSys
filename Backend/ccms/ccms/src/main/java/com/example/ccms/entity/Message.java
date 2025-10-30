package com.example.ccms.entity;

import com.example.ccms.enums.MessageTypeEnum;
import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "club_id")
    private Long clubId;

    private String content;

    @Enumerated(EnumType.STRING)
    private MessageTypeEnum type;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}