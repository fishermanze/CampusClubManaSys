package com.campusclub.message.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDTO {

    private Long id;
    private Long userId;
    private Integer notificationType;
    private String title;
    private String content;
    private Integer status;
    private Long relatedId;
    private String relatedType;
    private Boolean needPush;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}