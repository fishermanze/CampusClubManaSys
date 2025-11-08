package com.club.club.dto;

import lombok.Data;

@Data
public class MessageDTO {
    private Long receiverId;
    private String content;
    private Integer type;
}