package com.club.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.club.activity.entity.Activity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {
}