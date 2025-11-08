package com.club.club.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.club.club.dto.ClubApplyHandleDTO;
import com.club.club.entity.ClubApply;

public interface ClubApplyService extends IService<ClubApply> {
    // 提交加入社团申请
    boolean submitApply(Long clubId, Long applicantId);

    // 分页查询创始人的待审批列表
    IPage<ClubApply> getPendingApplyList(Page<ClubApply> page, Long founderId);

    // 处理申请（同意/拒绝）
    boolean handleApply(ClubApplyHandleDTO dto, Long operatorId);
}