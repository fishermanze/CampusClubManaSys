package com.club.activity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.club.activity.dto.ActivityCreateDTO;
import com.club.activity.entity.Activity;

public interface ActivityService extends IService<Activity> {
    boolean createActivity(ActivityCreateDTO dto, Long founderId);
}