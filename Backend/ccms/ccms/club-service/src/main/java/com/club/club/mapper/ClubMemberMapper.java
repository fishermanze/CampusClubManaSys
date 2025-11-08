package com.club.club.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.club.club.entity.ClubMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClubMemberMapper extends BaseMapper<ClubMember> {
    // 根据用户ID查询加入的所有社团成员记录
    List<ClubMember> selectByUserId(@Param("userId") Long userId);

    // 根据社团ID和用户ID查询成员记录（校验是否已加入）
    ClubMember selectByClubIdAndUserId(@Param("clubId") Long clubId, @Param("userId") Long userId);

    // 扩展：根据社团ID和角色筛选成员（角色可选，传null查询所有）
    List<ClubMember> selectByClubId(@Param("clubId") Long clubId, @Param("role") Integer role);

    // 扩展：统计社团成员总数
    Integer countByClubId(@Param("clubId") Long clubId);

    // 扩展：根据社团ID和用户ID删除成员记录（用于踢人功能）
    int deleteByClubIdAndUserId(@Param("clubId") Long clubId, @Param("userId") Long userId);
}