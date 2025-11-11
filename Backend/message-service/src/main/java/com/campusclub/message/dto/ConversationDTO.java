package com.campusclub.message.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConversationDTO {

    private Long userId;
    private String userName;
    private String userAvatar;
    private Integer unreadCount;
    private MessageDTO lastMessage;
    private Boolean isSticky;
    private Boolean isMuted;
    private LocalDateTime lastReadTime;
}