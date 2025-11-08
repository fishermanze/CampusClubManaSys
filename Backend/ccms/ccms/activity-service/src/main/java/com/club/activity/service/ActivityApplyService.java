package com.club.activity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.club.activity.dto.ActivityApplyDTO;
import com.club.activity.dto.ActivityHandleDTO;
import com.club.activity.entity.ActivityApply;

public interface ActivityApplyService extends IService<ActivityApply> {
    boolean submitApply(ActivityApplyDTO dto, Long applicantId);
    IPage<ActivityApply> getPendingApplyList(Page<ActivityApply> page, Long founderId);
    boolean handleApply(ActivityHandleDTO dto, Long operatorId);
}