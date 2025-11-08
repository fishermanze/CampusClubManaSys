package com.club.club.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.club.club.dto.*;
import com.club.club.entity.Club;
import com.club.club.entity.ClubApply;
import com.club.club.entity.ClubMember;
import com.club.club.service.ClubApplyService;
import com.club.club.service.ClubMemberService;
import com.club.club.service.ClubService;
import com.club.club.util.JwtUtils;
import com.club.common.result.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/club")
public class ClubController {

    @Resource
    private ClubService clubService;

    @Resource
    private ClubMemberService clubMemberService;

    @Resource
    private ClubApplyService clubApplyService; // 新增：申请服务

    @Resource
    private JwtUtils jwtUtils;

    // 从Token中解析用户ID
    private Long getCurrentUserId(String token) {
        token = token.substring(7); // 去除 "Bearer " 前缀
        return jwtUtils.getUserIdFromToken(token);
    }

    /**
     * 创建社团
     */
    @PostMapping("/create")
    public Result<Void> createClub(@Validated @RequestBody ClubCreateDTO dto,
                                   @RequestHeader("Authorization") String token) {
        Long founderId = getCurrentUserId(token);

        if (clubService.getClubByFounderId(founderId) != null) {
            return Result.fail("一个用户只能创建一个社团");
        }

        boolean success = clubService.createClub(dto, founderId);
        return success ? Result.success(null) : Result.fail("创建社团失败");
    }

    /**
     * 提交加入社团申请（替代原直接加入接口）
     */
    @PostMapping("/apply")
    public Result<Void> applyJoinClub(@Validated @RequestBody ClubApplyDTO dto,
                                      @RequestHeader("Authorization") String token) {
        Long applicantId = getCurrentUserId(token);
        try {
            boolean success = clubApplyService.submitApply(dto.getClubId(), applicantId);
            return success ? Result.success(null) : Result.fail("提交申请失败");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 创始人查询待审批申请列表
     */
    @GetMapping("/apply/pending")
    public Result<IPage<ClubApply>> getPendingApplies(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestHeader("Authorization") String token) {
        Long founderId = getCurrentUserId(token);
        Page<ClubApply> page = new Page<>(pageNum, pageSize);
        IPage<ClubApply> applyPage = clubApplyService.getPendingApplyList(page, founderId);
        return Result.success(applyPage);
    }

    /**
     * 创始人处理申请（同意/拒绝）
     */
    @PostMapping("/apply/handle")
    public Result<Void> handleClubApply(@Validated @RequestBody ClubApplyHandleDTO dto,
                                        @RequestHeader("Authorization") String token) {
        Long operatorId = getCurrentUserId(token); // 必须是社团创始人
        try {
            boolean success = clubApplyService.handleApply(dto, operatorId);
            return success ? Result.success(null) : Result.fail("处理申请失败");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 踢人功能
     */
    @PostMapping("/kick")
    public Result<Void> kickMember(@Validated @RequestBody ClubKickDTO dto,
                                   @RequestHeader("Authorization") String token) {
        Long operatorId = getCurrentUserId(token);

        try {
            boolean success = clubMemberService.kickMember(
                    dto.getClubId(), operatorId, dto.getKickedUserId()
            );
            return success ? Result.success(null) : Result.fail("踢人失败");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 查询用户加入的所有社团
     */
    @GetMapping("/my-clubs")
    public Result<List<ClubMember>> getMyClubs(@RequestHeader("Authorization") String token) {
        Long userId = getCurrentUserId(token);
        List<ClubMember> memberList = clubMemberService.getMemberByUserId(userId);
        return Result.success(memberList);
    }

    /**
     * 分页查询所有社团
     */
    @GetMapping("/list")
    public Result<IPage<Club>> getClubList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Club> page = new Page<>(pageNum, pageSize);
        IPage<Club> clubPage = clubService.page(page);
        return Result.success(clubPage);
    }

    /**
     * 查询社团详情
     */
    @GetMapping("/{clubId}")
    public Result<Club> getClubDetail(@PathVariable Long clubId) {
        Club club = clubService.getById(clubId);
        if (club == null) {
            return Result.fail("社团不存在");
        }
        return Result.success(club);
    }

    /**
     * 查询社团成员列表（支持角色筛选）
     */
    @GetMapping("/{clubId}/members")
    public Result<List<ClubMember>> getClubMembers(
            @PathVariable Long clubId,
            @RequestParam(required = false) Integer role) {
        try {
            List<ClubMember> members = clubMemberService.getMembersByClubId(clubId, role);
            return Result.success(members);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 统计社团成员数量
     */
    @GetMapping("/{clubId}/member-count")
    public Result<Integer> getClubMemberCount(@PathVariable Long clubId) {
        try {
            Integer count = clubMemberService.countMembersByClubId(clubId);
            return Result.success(count);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }
}