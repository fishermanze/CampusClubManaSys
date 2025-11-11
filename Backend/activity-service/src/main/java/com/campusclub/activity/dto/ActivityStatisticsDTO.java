package com.campusclub.activity.dto;

import lombok.Data;

@Data
public class ActivityStatisticsDTO {

    // 总活动数
    private long totalActivities;

    // 进行中的活动数
    private long ongoingActivities;

    // 已结束的活动数
    private long endedActivities;

    // 已发布的活动数
    private long publishedActivities;

    // 草稿活动数
    private long draftActivities;

    // 总参与人次
    private long totalParticipants;

    // 总浏览量
    private long totalViews;

    // 总评论数
    private long totalComments;

    // 平均参与率
    private double averageParticipationRate;
}