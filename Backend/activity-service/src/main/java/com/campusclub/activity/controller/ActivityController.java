package com.campusclub.activity.controller;

import com.campusclub.activity.dto.ActivityDTO;
import com.campusclub.activity.dto.ActivityStatisticsDTO;
import com.campusclub.activity.entity.Activity;
import com.campusclub.activity.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    /**
     * 创建活动
     */
    @PostMapping
    public ResponseEntity<Activity> createActivity(@RequestBody Activity activity) {
        return ResponseEntity.ok(activityService.createActivity(activity));
    }

    /**
     * 更新活动
     */
    @PutMapping("/{id}")
    public ResponseEntity<Activity> updateActivity(@PathVariable Long id, @RequestBody Activity activity) {
        activity.setId(id);
        return ResponseEntity.ok(activityService.updateActivity(activity));
    }

    /**
     * 获取活动详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivityById(@PathVariable Long id) {
        // 从请求头或Token中获取当前用户ID
        Long currentUserId = getCurrentUserId();
        return ResponseEntity.ok(activityService.getActivityById(id, currentUserId));
    }

    /**
     * 删除活动
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivityById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 更新活动状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateActivityStatus(@PathVariable Long id, @RequestParam Integer status) {
        activityService.updateActivityStatus(id, status);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取活动列表
     */
    @GetMapping
    public ResponseEntity<Page<ActivityDTO>> getActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader) {
        
        Pageable pageable = PageRequest.of(normalizePage(page), size, Sort.by(getSortOrder(sort)));
        Long currentUserId = getCurrentUserId(userIdHeader);
        return ResponseEntity.ok(activityService.getActivities(pageable, currentUserId));
    }

    /**
     * 获取社团活动
     */
    @GetMapping("/club/{clubId}")
    public ResponseEntity<Page<ActivityDTO>> getClubActivities(
            @PathVariable Long clubId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        
        Pageable pageable = PageRequest.of(normalizePage(page), size, Sort.by(getSortOrder(sort)));
        Long currentUserId = getCurrentUserId();
        return ResponseEntity.ok(activityService.getClubActivities(clubId, pageable, currentUserId));
    }

    /**
     * 获取用户参与的活动
     */
    @GetMapping("/user/{userId}/participated")
    public ResponseEntity<Page<ActivityDTO>> getUserParticipatedActivities(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(normalizePage(page), size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(activityService.getUserParticipatedActivities(userId, pageable));
    }

    /**
     * 获取用户创建的活动
     */
    @GetMapping("/user/{userId}/created")
    public ResponseEntity<Page<ActivityDTO>> getUserCreatedActivities(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(normalizePage(page), size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(activityService.getUserCreatedActivities(userId, pageable));
    }

    /**
     * 搜索活动
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ActivityDTO>> searchActivities(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(normalizePage(page), size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Long currentUserId = getCurrentUserId();
        return ResponseEntity.ok(activityService.searchActivities(keyword, pageable, currentUserId));
    }

    /**
     * 获取即将开始的活动
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<ActivityDTO>> getUpcomingActivities(
            @RequestParam(defaultValue = "5") int limit) {
        
        Long currentUserId = getCurrentUserId();
        return ResponseEntity.ok(activityService.getUpcomingActivities(limit, currentUserId));
    }

    /**
     * 获取正在进行的活动
     */
    @GetMapping("/current")
    public ResponseEntity<List<ActivityDTO>> getCurrentActivities(
            @RequestParam(defaultValue = "5") int limit) {
        
        Long currentUserId = getCurrentUserId();
        return ResponseEntity.ok(activityService.getCurrentActivities(limit, currentUserId));
    }

    /**
     * 根据标签获取活动
     */
    @GetMapping("/tag/{tag}")
    public ResponseEntity<Page<ActivityDTO>> getActivitiesByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(normalizePage(page), size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Long currentUserId = getCurrentUserId();
        return ResponseEntity.ok(activityService.getActivitiesByTag(tag, pageable, currentUserId));
    }

    /**
     * 增加浏览量
     */
    @PostMapping("/{id}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long id) {
        activityService.incrementViewCount(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 点赞活动
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeActivity(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();
        activityService.likeActivity(id, currentUserId);
        return ResponseEntity.ok().build();
    }

    /**
     * 取消点赞活动
     */
    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> unlikeActivity(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();
        activityService.unlikeActivity(id, currentUserId);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取活动统计
     */
    @GetMapping("/statistics/club/{clubId}")
    public ResponseEntity<ActivityStatisticsDTO> getActivityStatistics(@PathVariable Long clubId) {
        return ResponseEntity.ok(activityService.getActivityStatistics(clubId));
    }

    /**
     * 自动更新活动状态（由定时任务调用）
     */
    @PostMapping("/update-status/auto")
    public ResponseEntity<Void> updateActivityStatusAutomatically() {
        activityService.updateActivityStatusAutomatically();
        return ResponseEntity.ok().build();
    }

    // 辅助方法
    private Long getCurrentUserId() {
        // 从请求头中获取用户ID（由API Gateway的AuthenticationFilter传递）
        // 注意：在Spring WebFlux中需要使用其他方式获取请求头
        // 这里返回null，允许未登录用户访问活动列表
        return null;
    }
    
    // 辅助方法 - 从请求头字符串解析用户ID
    private Long getCurrentUserId(String userIdHeader) {
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private Sort.Order[] getSortOrder(String sort) {
        // 支持多字段排序，格式：field1,direction1;field2,direction2
        if (sort == null || sort.isEmpty()) {
            return new Sort.Order[]{
                Sort.Order.desc("createdAt"),
                Sort.Order.asc("id")
            };
        }
        
        // 如果包含分号，说明是多字段排序
        if (sort.contains(";")) {
            String[] sortPairs = sort.split(";");
            Sort.Order[] orders = new Sort.Order[sortPairs.length];
            for (int i = 0; i < sortPairs.length; i++) {
                String[] parts = sortPairs[i].split(",");
                String property = parts[0].trim();
                String direction = parts.length > 1 ? parts[1].trim() : "desc";
                orders[i] = direction.equalsIgnoreCase("asc") 
                        ? Sort.Order.asc(property) 
                        : Sort.Order.desc(property);
            }
            return orders;
        } else {
            // 单字段排序
            String[] parts = sort.split(",");
            String property = parts[0].trim();
            String direction = parts.length > 1 ? parts[1].trim() : "desc";
            // 单字段排序时，默认添加id作为第二排序字段
            return new Sort.Order[]{
                direction.equalsIgnoreCase("asc") 
                        ? Sort.Order.asc(property) 
                        : Sort.Order.desc(property),
                Sort.Order.asc("id")
            };
        }
    }

    /**
     * 兼容前端可能传入的1基页码：当page>=1时，自动转换为0基页码
     */
    private int normalizePage(int page) {
        if (page <= 0) {
            return 0;
        }
        return page - 1;
    }
}