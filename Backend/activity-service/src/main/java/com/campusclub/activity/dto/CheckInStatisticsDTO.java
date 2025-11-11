package com.campusclub.activity.dto;

import lombok.Data;

@Data
public class CheckInStatisticsDTO {

    // 应到人数
    private long expectedParticipants;

    // 实到人数
    private long actualParticipants;

    // 签到率
    private double checkInRate;

    // 未签到人数
    private long absentParticipants;

    // 已签到人数
    private long checkedInCount;

    // 已退签人数
    private long checkedOutCount;

    // 签到统计详情（可选）
    private String details;
}