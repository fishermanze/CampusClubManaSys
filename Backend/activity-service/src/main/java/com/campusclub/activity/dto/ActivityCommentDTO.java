package com.campusclub.activity.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActivityCommentDTO {

    private Long id;

    private Long activityId;

    private Long userId;

    private String userName;

    private String userAvatar;

    private String content;

    private Long parentId;

    private Integer likesCount;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean isLiked;

    private List<ActivityCommentDTO> replies;

    private Integer replyCount;
}