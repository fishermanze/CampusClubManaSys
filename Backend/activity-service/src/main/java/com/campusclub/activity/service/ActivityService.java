package com.campusclub.activity.service;

import com.campusclub.activity.dto.ActivityDTO;
import com.campusclub.activity.dto.ActivityStatisticsDTO;
import com.campusclub.activity.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityService {

    /**
     * 创建活动
     */
    Activity createActivity(Activity activity);

    /**
     * 更新活动
     */
    Activity updateActivity(Activity activity);

    /**
     * 获取活动详情
     */
    ActivityDTO getActivityById(Long id, Long currentUserId);

    /**
     * 根据ID删除活动
     */
    void deleteActivityById(Long id);

    /**
     * 更新活动状态
     */
    void updateActivityStatus(Long id, Integer status);

    /**
     * 分页获取活动列表
     */
    Page<ActivityDTO> getActivities(Pageable pageable, Long currentUserId);

    /**
     * 获取社团活动列表
     */
    Page<ActivityDTO> getClubActivities(Long clubId, Pageable pageable, Long currentUserId);

    /**
     * 获取用户参与的活动列表
     */
    Page<ActivityDTO> getUserParticipatedActivities(Long userId, Pageable pageable);

    /**
     * 获取用户创建的活动列表
     */
    Page<ActivityDTO> getUserCreatedActivities(Long userId, Pageable pageable);

    /**
     * 搜索活动
     */
    Page<ActivityDTO> searchActivities(String keyword, Pageable pageable, Long currentUserId);

    /**
     * 获取即将开始的活动
     */
    List<ActivityDTO> getUpcomingActivities(int limit, Long currentUserId);

    /**
     * 获取正在进行的活动
     */
    List<ActivityDTO> getCurrentActivities(int limit, Long currentUserId);

    /**
     * 根据标签获取活动
     */
    Page<ActivityDTO> getActivitiesByTag(String tag, Pageable pageable, Long currentUserId);

    /**
     * 增加活动浏览量
     */
    void incrementViewCount(Long id);

    /**
     * 点赞活动
     */
    void likeActivity(Long activityId, Long userId);

    /**
     * 取消点赞活动
     */
    void unlikeActivity(Long activityId, Long userId);

    /**
     * 统计活动数据
     */
    ActivityStatisticsDTO getActivityStatistics(Long clubId);

    /**
     * 自动更新活动状态
     */
    void updateActivityStatusAutomatically();

    /**
     * 检查活动是否已满
     */
    boolean isActivityFull(Long id);

    /**
     * 检查用户是否参与了活动
     */
    boolean isUserParticipated(Long activityId, Long userId);

    /**
     * 检查用户是否点赞了活动
     */
    boolean isUserLiked(Long activityId, Long userId);
}