package com.campusclub.message.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {

    private Long id;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private Long receiverId;
    private Integer messageType;
    private String content;
    private Integer status;
    private Long relatedId;
    private String relatedType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}