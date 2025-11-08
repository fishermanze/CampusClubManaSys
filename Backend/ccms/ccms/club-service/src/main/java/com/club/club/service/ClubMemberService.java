package com.club.club.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.club.club.entity.ClubMember;
import java.util.List;

public interface ClubMemberService extends IService<ClubMember> {
    // 加入社团
    boolean joinClub(Long clubId, Long userId);

    // 查询用户加入的所有社团
    List<ClubMember> getMemberByUserId(Long userId);

    // 校验是否已加入社团
    boolean isMemberExists(Long clubId, Long userId);

    // 新增：踢人功能（操作者ID、被踢用户ID、社团ID）
    boolean kickMember(Long clubId, Long operatorId, Long kickedUserId);

    // 新增：查询社团的所有成员（支持角色筛选，用于管理场景）
    List<ClubMember> getMembersByClubId(Long clubId, Integer role);

    // 新增：统计社团成员数量（用于社团详情展示）
    Integer countMembersByClubId(Long clubId);
}