package com.club.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.activity.entity.ActivityApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ActivityApplyMapper extends BaseMapper<ActivityApply> {
    IPage<ActivityApply> selectPendingByFounderId(Page<ActivityApply> page, @Param("founderId") Long founderId);
    ActivityApply selectByActivityIdAndApplicantId(@Param("activityId") Long activityId, @Param("applicantId") Long applicantId);
}