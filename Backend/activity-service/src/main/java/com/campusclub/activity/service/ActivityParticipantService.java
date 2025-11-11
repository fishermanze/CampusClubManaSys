package com.campusclub.activity.service;

import com.campusclub.activity.dto.ActivityParticipantDTO;
import com.campusclub.activity.dto.CheckInStatisticsDTO;
import com.campusclub.activity.entity.ActivityParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityParticipantService {

    /**
     * 报名参加活动
     */
    ActivityParticipant enrollActivity(ActivityParticipant participant);

    /**
     * 取消报名
     */
    void cancelEnrollment(Long activityId, Long userId);

    /**
     * 审批参与申请
     */
    void approveParticipation(Long activityId, Long userId, Integer status, String remark);

    /**
     * 签到
     */
    void checkIn(Long activityId, Long userId);

    /**
     * 退签
     */
    void checkOut(Long activityId, Long userId);

    /**
     * 获取参与记录
     */
    ActivityParticipantDTO getParticipation(Long activityId, Long userId);

    /**
     * 获取活动参与者列表
     */
    Page<ActivityParticipantDTO> getActivityParticipants(Long activityId, Integer status, Pageable pageable);

    /**
     * 获取用户参与的活动列表
     */
    Page<ActivityParticipantDTO> getUserParticipations(Long userId, Pageable pageable);

    /**
     * 批量审批
     */
    void batchApproveParticipations(Long activityId, List<Long> userIds, Integer status, String remark);

    /**
     * 批量签到
     */
    void batchCheckIn(Long activityId, List<Long> userIds);

    /**
     * 导出参与者名单
     */
    byte[] exportParticipants(Long activityId, Integer status);

    /**
     * 统计活动参与人数
     */
    long countParticipants(Long activityId, Integer status);

    /**
     * 检查用户是否已参与活动
     */
    boolean hasParticipated(Long activityId, Long userId);

    /**
     * 获取用户在活动中的状态
     */
    Integer getUserParticipationStatus(Long activityId, Long userId);

    /**
     * 更新参与信息
     */
    void updateEnrollmentInfo(Long activityId, Long userId, String enrollmentInfo);

    /**
     * 获取活动签到统计
     */
    CheckInStatisticsDTO getCheckInStatistics(Long activityId);
}