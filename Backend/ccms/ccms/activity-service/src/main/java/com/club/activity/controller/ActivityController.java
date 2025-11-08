package com.club.activity.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.club.activity.dto.ActivityCreateDTO;
import com.club.activity.dto.ActivityApplyDTO;
import com.club.activity.dto.ActivityHandleDTO;
import com.club.activity.entity.Activity;
import com.club.activity.entity.ActivityApply;
import com.club.activity.service.ActivityApplyService;
import com.club.activity.service.ActivityService;
import com.club.club.util.JwtUtils;
import com.club.common.result.Result;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Resource
    private ActivityService activityService;

    @Resource
    private ActivityApplyService activityApplyService;

    @Resource
    private JwtUtils jwtUtils;

    private Long getCurrentUserId(String token) {
        token = token.substring(7);
        return jwtUtils.getUserIdFromToken(token);
    }

    @PostMapping("/create")
    public Result<Void> createActivity(@Validated @RequestBody ActivityCreateDTO dto,
                                       @RequestHeader("Authorization") String token) {
        Long founderId = getCurrentUserId(token);
        boolean success = activityService.createActivity(dto, founderId);
        return success ? Result.success(null) : Result.fail("创建活动失败");
    }

    @PostMapping("/apply")
    public Result<Void> applyActivity(@Validated @RequestBody ActivityApplyDTO dto,
                                      @RequestHeader("Authorization") String token) {
        Long applicantId = getCurrentUserId(token);
        try {
            boolean success = activityApplyService.submitApply(dto, applicantId);
            return success ? Result.success(null) : Result.fail("提交申请失败");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/apply/pending")
    public Result<IPage<ActivityApply>> getPendingApplies(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestHeader("Authorization") String token) {
        Long founderId = getCurrentUserId(token);
        Page<ActivityApply> page = new Page<>(pageNum, pageSize);
        IPage<ActivityApply> applyPage = activityApplyService.getPendingApplyList(page, founderId);
        return Result.success(applyPage);
    }

    @PostMapping("/apply/handle")
    public Result<Void> handleActivityApply(@Validated @RequestBody ActivityHandleDTO dto,
                                            @RequestHeader("Authorization") String token) {
        Long operatorId = getCurrentUserId(token);
        try {
            boolean success = activityApplyService.handleApply(dto, operatorId);
            return success ? Result.success(null) : Result.fail("处理申请失败");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/{activityId}")
    public Result<Activity> getActivityDetail(@PathVariable Long activityId) {
        Activity activity = activityService.getById(activityId);
        if (activity == null) {
            return Result.fail("活动不存在");
        }
        return Result.success(activity);
    }

    @GetMapping("/club/{clubId}")
    public Result<List<Activity>> getClubActivities(@PathVariable Long clubId) {
        // 修正：使用 MyBatis-Plus 标准 LambdaQueryWrapper 构造查询条件
        LambdaQueryWrapper<Activity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Activity::getClubId, clubId)  // 匹配目标社团ID
                .eq(Activity::getStatus, 1);      // 筛选状态为“发布中”的活动
        List<Activity> activities = activityService.list(queryWrapper);
        return Result.success(activities);
    }
}