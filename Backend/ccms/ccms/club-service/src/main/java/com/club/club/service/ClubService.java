package com.club.club.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.club.club.dto.ClubCreateDTO;
import com.club.club.entity.Club;

public interface ClubService extends IService<Club> {
    // 创建社团
    boolean createClub(ClubCreateDTO dto, Long founderId);

    // 根据创始人ID查询社团
    Club getClubByFounderId(Long founderId);

    // 根据社团名称查询
    Club getClubByName(String name);
}