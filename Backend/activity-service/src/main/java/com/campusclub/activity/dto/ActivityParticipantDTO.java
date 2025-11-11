package com.campusclub.activity.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActivityParticipantDTO {

    private Long id;

    private Long activityId;

    private String activityTitle;

    private Long userId;

    private String userName;

    private String userAvatar;

    private Integer status;

    private String statusText;

    private LocalDateTime enrollmentTime;

    private LocalDateTime approvalTime;

    private Long approvedBy;

    private String approvedByName;

    private String approvalRemark;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private String enrollmentInfo;
}