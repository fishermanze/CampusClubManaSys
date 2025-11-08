package com.club.club.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.club.club.entity.Club;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClubMapper extends BaseMapper<Club> {
    // 根据创始人ID查询社团
    default Club selectByFounderId(Long founderId) {
        LambdaQueryWrapper<Club> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Club::getFounderId, founderId);
        return selectOne(queryWrapper);
    }

    // 根据社团名称查询
    default Club selectByName(String name) {
        LambdaQueryWrapper<Club> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Club::getName, name);
        return selectOne(queryWrapper);
    }
}