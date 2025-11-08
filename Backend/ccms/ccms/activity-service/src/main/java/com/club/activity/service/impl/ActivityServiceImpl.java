package com.club.activity.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.club.activity.dto.ActivityCreateDTO;
import com.club.activity.entity.Activity;
import com.club.activity.mapper.ActivityMapper;
import com.club.activity.service.ActivityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createActivity(ActivityCreateDTO dto, Long founderId) {
        Activity activity = new Activity();
        activity.setClubId(dto.getClubId());
        activity.setFounderId(founderId);
        activity.setTitle(dto.getTitle());
        activity.setContent(dto.getContent());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setLocation(dto.getLocation());
        activity.setStatus(1); // 发布中
        activity.setCreateTime(LocalDateTime.now());
        return save(activity);
    }
}