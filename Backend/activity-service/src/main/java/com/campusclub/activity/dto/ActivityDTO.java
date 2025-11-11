package com.campusclub.activity.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActivityDTO {

    private Long id;

    private String title;

    private String description;

    private String content;

    private String coverImage;

    private List<String> images;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String location;

    private Long organizerId;

    private String organizerName;

    private Long clubId;

    private String clubName;

    private Integer status;

    private String statusText;

    private Integer maxParticipants;

    private Integer currentParticipants;

    private String tags;

    private Integer viewCount;

    private Integer commentCount;

    private Integer likeCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean isLiked;

    private Boolean isParticipated;

    private Boolean isOrganizer;

    private Boolean isClubMember;
}