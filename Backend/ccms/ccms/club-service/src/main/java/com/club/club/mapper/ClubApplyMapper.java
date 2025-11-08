package com.club.club.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.club.entity.ClubApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ClubApplyMapper extends BaseMapper<ClubApply> {
    // 分页查询创始人的待审批列表
    IPage<ClubApply> selectPendingByFounderId(Page<ClubApply> page, @Param("founderId") Long founderId);

    // 查询用户是否已提交过该社团的申请
    ClubApply selectByClubIdAndApplicantId(@Param("clubId") Long clubId, @Param("applicantId") Long applicantId);
}
