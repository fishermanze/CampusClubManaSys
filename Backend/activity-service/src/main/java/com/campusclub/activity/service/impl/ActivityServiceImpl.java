package com.campusclub.activity.service.impl;

import com.campusclub.activity.dto.ActivityDTO;
import com.campusclub.activity.dto.ActivityStatisticsDTO;
import com.campusclub.activity.entity.Activity;
import com.campusclub.activity.repository.ActivityRepository;
import com.campusclub.activity.service.ActivityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public Activity createActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    @Override
    @Transactional
    public Activity updateActivity(Activity activity) {
        // 验证活动是否存在
        Activity existingActivity = activityRepository.findById(activity.getId())
                .orElseThrow(() -> new RuntimeException("活动不存在"));

        // 更新非空字段
        if (activity.getTitle() != null) existingActivity.setTitle(activity.getTitle());
        if (activity.getDescription() != null) existingActivity.setDescription(activity.getDescription());
        if (activity.getContent() != null) existingActivity.setContent(activity.getContent());
        if (activity.getCoverImage() != null) existingActivity.setCoverImage(activity.getCoverImage());
        if (activity.getImages() != null) existingActivity.setImages(activity.getImages());
        if (activity.getStartTime() != null) existingActivity.setStartTime(activity.getStartTime());
        if (activity.getEndTime() != null) existingActivity.setEndTime(activity.getEndTime());
        if (activity.getLocation() != null) existingActivity.setLocation(activity.getLocation());
        if (activity.getMaxParticipants() != null) existingActivity.setMaxParticipants(activity.getMaxParticipants());
        if (activity.getTags() != null) existingActivity.setTags(activity.getTags());
        if (activity.getStatus() != null) existingActivity.setStatus(activity.getStatus());

        return activityRepository.save(existingActivity);
    }

    @Override
    public ActivityDTO getActivityById(Long id, Long currentUserId) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("活动不存在"));

        ActivityDTO dto = modelMapper.map(activity, ActivityDTO.class);

        // 设置状态文本
        setActivityStatusText(dto);

        // 设置用户相关信息
        dto.setIsParticipated(isUserParticipated(id, currentUserId));
        dto.setIsLiked(isUserLiked(id, currentUserId));
        dto.setIsOrganizer(currentUserId != null && currentUserId.equals(activity.getOrganizerId()));

        return dto;
    }

    @Override
    @Transactional
    public void deleteActivityById(Long id) {
        // 软删除，更新状态为已删除
        activityRepository.updateActivityStatus(id, 4); // 假设4代表已删除
    }

    @Override
    @Transactional
    public void updateActivityStatus(Long id, Integer status) {
        activityRepository.updateActivityStatus(id, status);
    }

    @Override
    public Page<ActivityDTO> getActivities(Pageable pageable, Long currentUserId) {
        Page<Activity> activities = activityRepository.findAll(pageable);
        return activities.map(activity -> {
            ActivityDTO dto = modelMapper.map(activity, ActivityDTO.class);
            setActivityStatusText(dto);
            dto.setIsParticipated(isUserParticipated(activity.getId(), currentUserId));
            dto.setIsLiked(isUserLiked(activity.getId(), currentUserId));
            return dto;
        });
    }

    @Override
    public Page<ActivityDTO> getClubActivities(Long clubId, Pageable pageable, Long currentUserId) {
        Page<Activity> activities = activityRepository.findAll(
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("clubId"), clubId),
                pageable);
        return activities.map(activity -> {
            ActivityDTO dto = modelMapper.map(activity, ActivityDTO.class);
            setActivityStatusText(dto);
            dto.setIsParticipated(isUserParticipated(activity.getId(), currentUserId));
            dto.setIsLiked(isUserLiked(activity.getId(), currentUserId));
            return dto;
        });
    }

    @Override
    public Page<ActivityDTO> getUserParticipatedActivities(Long userId, Pageable pageable) {
        // 需要结合ActivityParticipantRepository，这里简化实现
        return Page.empty();
    }

    @Override
    public Page<ActivityDTO> getUserCreatedActivities(Long userId, Pageable pageable) {
        Page<Activity> activities = activityRepository.findAll(
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("organizerId"), userId),
                pageable);
        return activities.map(activity -> {
            ActivityDTO dto = modelMapper.map(activity, ActivityDTO.class);
            setActivityStatusText(dto);
            return dto;
        });
    }

    @Override
    public Page<ActivityDTO> searchActivities(String keyword, Pageable pageable, Long currentUserId) {
        List<Activity> activities = activityRepository.searchActivities(keyword);
        // 简化实现，实际应该使用Page
        List<ActivityDTO> dtoList = new ArrayList<>();
        for (Activity activity : activities) {
            ActivityDTO dto = modelMapper.map(activity, ActivityDTO.class);
            setActivityStatusText(dto);
            dto.setIsParticipated(isUserParticipated(activity.getId(), currentUserId));
            dto.setIsLiked(isUserLiked(activity.getId(), currentUserId));
            dtoList.add(dto);
        }
        return Page.empty();
    }

    @Override
    public List<ActivityDTO> getUpcomingActivities(int limit, Long currentUserId) {
        List<Activity> activities = activityRepository.findUpcomingActivities(LocalDateTime.now());
        List<ActivityDTO> dtoList = new ArrayList<>();
        for (Activity activity : activities.subList(0, Math.min(activities.size(), limit))) {
            ActivityDTO dto = modelMapper.map(activity, ActivityDTO.class);
            setActivityStatusText(dto);
            dto.setIsParticipated(isUserParticipated(activity.getId(), currentUserId));
            dto.setIsLiked(isUserLiked(activity.getId(), currentUserId));
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public List<ActivityDTO> getCurrentActivities(int limit, Long currentUserId) {
        List<Activity> activities = activityRepository.findCurrentActivities(LocalDateTime.now());
        List<ActivityDTO> dtoList = new ArrayList<>();
        for (Activity activity : activities.subList(0, Math.min(activities.size(), limit))) {
            ActivityDTO dto = modelMapper.map(activity, ActivityDTO.class);
            setActivityStatusText(dto);
            dto.setIsParticipated(isUserParticipated(activity.getId(), currentUserId));
            dto.setIsLiked(isUserLiked(activity.getId(), currentUserId));
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public Page<ActivityDTO> getActivitiesByTag(String tag, Pageable pageable, Long currentUserId) {
        List<Activity> activities = activityRepository.findByTag(tag);
        // 简化实现
        return Page.empty();
    }

    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        // 确认活动存在，但移除对不存在的viewCount字段的操作
        activityRepository.findById(id).orElseThrow(() -> new RuntimeException("活动不存在"));
        // 可以考虑在Activity实体类中添加viewCount字段来实现真正的浏览量统计
    }

    @Override
    public void likeActivity(Long activityId, Long userId) {
        // 需要实现点赞功能，涉及Redis或数据库表
        System.out.println("用户" + userId + "点赞活动" + activityId);
    }

    @Override
    public void unlikeActivity(Long activityId, Long userId) {
        // 需要实现取消点赞功能
        System.out.println("用户" + userId + "取消点赞活动" + activityId);
    }

    @Override
    public ActivityStatisticsDTO getActivityStatistics(Long clubId) {
        ActivityStatisticsDTO statistics = new ActivityStatisticsDTO();
        // 简化实现，实际需要复杂的统计查询
        statistics.setTotalActivities(activityRepository.count());
        statistics.setOngoingActivities(0);
        statistics.setEndedActivities(0);
        statistics.setPublishedActivities(0);
        statistics.setDraftActivities(0);
        statistics.setTotalParticipants(0);
        statistics.setTotalViews(0);
        statistics.setTotalComments(0);
        statistics.setAverageParticipationRate(0.0);
        return statistics;
    }

    @Override
    @Transactional
    public void updateActivityStatusAutomatically() {
        LocalDateTime now = LocalDateTime.now();
        // 更新草稿为已发布（如果当前时间超过预定发布时间）
        // 更新已发布为进行中（如果当前时间超过开始时间）
        // 更新进行中为已结束（如果当前时间超过结束时间）
    }

    @Override
    public boolean isActivityFull(Long id) {
        Activity activity = activityRepository.findById(id).orElseThrow(() -> new RuntimeException("活动不存在"));
        return activity.getMaxParticipants() > 0 &&
                activity.getCurrentParticipants() >= activity.getMaxParticipants();
    }

    @Override
    public boolean isUserParticipated(Long activityId, Long userId) {
        // 需要查询ActivityParticipant表
        return false;
    }

    @Override
    public boolean isUserLiked(Long activityId, Long userId) {
        // 需要查询点赞记录
        return false;
    }

    private void setActivityStatusText(ActivityDTO dto) {
        switch (dto.getStatus()) {
            case 0: dto.setStatusText("草稿"); break;
            case 1: dto.setStatusText("已发布"); break;
            case 2: dto.setStatusText("进行中"); break;
            case 3: dto.setStatusText("已结束"); break;
            case 4: dto.setStatusText("已删除"); break;
            default: dto.setStatusText("未知");
        }
    }
}