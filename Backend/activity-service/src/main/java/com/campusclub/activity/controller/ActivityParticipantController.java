package com.campusclub.activity.controller;

import com.campusclub.activity.dto.ActivityParticipantDTO;
import com.campusclub.activity.dto.CheckInStatisticsDTO;
import com.campusclub.activity.entity.ActivityParticipant;
import com.campusclub.activity.service.ActivityParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participations")
public class ActivityParticipantController {

    @Autowired
    private ActivityParticipantService participantService;

    /**
     * 报名参加活动
     */
    @PostMapping
    public ResponseEntity<ActivityParticipant> enrollActivity(@RequestBody ActivityParticipant participant) {
        return ResponseEntity.ok(participantService.enrollActivity(participant));
    }

    /**
     * 取消报名
     */
    @DeleteMapping("/activity/{activityId}/user/{userId}")
    public ResponseEntity<Void> cancelEnrollment(@PathVariable Long activityId, @PathVariable Long userId) {
        participantService.cancelEnrollment(activityId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 审批参与申请
     */
    @PutMapping("/activity/{activityId}/user/{userId}/approve")
    public ResponseEntity<Void> approveParticipation(
            @PathVariable Long activityId,
            @PathVariable Long userId,
            @RequestParam Integer status,
            @RequestParam(required = false) String remark) {
        
        participantService.approveParticipation(activityId, userId, status, remark);
        return ResponseEntity.ok().build();
    }

    /**
     * 签到
     */
    @PostMapping("/activity/{activityId}/user/{userId}/check-in")
    public ResponseEntity<Void> checkIn(@PathVariable Long activityId, @PathVariable Long userId) {
        participantService.checkIn(activityId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 退签
     */
    @PostMapping("/activity/{activityId}/user/{userId}/check-out")
    public ResponseEntity<Void> checkOut(@PathVariable Long activityId, @PathVariable Long userId) {
        participantService.checkOut(activityId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取参与记录
     */
    @GetMapping("/activity/{activityId}/user/{userId}")
    public ResponseEntity<ActivityParticipantDTO> getParticipation(@PathVariable Long activityId, @PathVariable Long userId) {
        return ResponseEntity.ok(participantService.getParticipation(activityId, userId));
    }

    /**
     * 获取活动参与者列表
     */
    @GetMapping("/activity/{activityId}")
    public ResponseEntity<Page<ActivityParticipantDTO>> getActivityParticipants(
            @PathVariable Long activityId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "enrollmentTime,desc") String sort) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(getSortOrder(sort)));
        return ResponseEntity.ok(participantService.getActivityParticipants(activityId, status, pageable));
    }

    /**
     * 获取用户参与的活动列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ActivityParticipantDTO>> getUserParticipations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "enrollmentTime,desc") String sort) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(getSortOrder(sort)));
        return ResponseEntity.ok(participantService.getUserParticipations(userId, pageable));
    }

    /**
     * 批量审批
     */
    @PutMapping("/activity/{activityId}/batch-approve")
    public ResponseEntity<Void> batchApproveParticipations(
            @PathVariable Long activityId,
            @RequestBody List<Long> userIds,
            @RequestParam Integer status,
            @RequestParam(required = false) String remark) {
        
        participantService.batchApproveParticipations(activityId, userIds, status, remark);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量签到
     */
    @PostMapping("/activity/{activityId}/batch-check-in")
    public ResponseEntity<Void> batchCheckIn(
            @PathVariable Long activityId,
            @RequestBody List<Long> userIds) {
        
        participantService.batchCheckIn(activityId, userIds);
        return ResponseEntity.ok().build();
    }

    /**
     * 导出参与者名单
     */
    @GetMapping("/activity/{activityId}/export")
    public ResponseEntity<byte[]> exportParticipants(
            @PathVariable Long activityId,
            @RequestParam(required = false) Integer status) {
        
        byte[] data = participantService.exportParticipants(activityId, status);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=participants.xlsx")
                .body(data);
    }

    /**
     * 统计活动参与人数
     */
    @GetMapping("/activity/{activityId}/count")
    public ResponseEntity<Long> countParticipants(
            @PathVariable Long activityId,
            @RequestParam(required = false) Integer status) {
        
        return ResponseEntity.ok(participantService.countParticipants(activityId, status != null ? status : 1));
    }

    /**
     * 检查用户是否已参与活动
     */
    @GetMapping("/activity/{activityId}/user/{userId}/exists")
    public ResponseEntity<Boolean> hasParticipated(@PathVariable Long activityId, @PathVariable Long userId) {
        return ResponseEntity.ok(participantService.hasParticipated(activityId, userId));
    }

    /**
     * 获取用户在活动中的状态
     */
    @GetMapping("/activity/{activityId}/user/{userId}/status")
    public ResponseEntity<Integer> getUserParticipationStatus(@PathVariable Long activityId, @PathVariable Long userId) {
        return ResponseEntity.ok(participantService.getUserParticipationStatus(activityId, userId));
    }

    /**
     * 更新参与信息
     */
    @PutMapping("/activity/{activityId}/user/{userId}/info")
    public ResponseEntity<Void> updateEnrollmentInfo(
            @PathVariable Long activityId,
            @PathVariable Long userId,
            @RequestParam String enrollmentInfo) {
        
        participantService.updateEnrollmentInfo(activityId, userId, enrollmentInfo);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取活动签到统计
     */
    @GetMapping("/activity/{activityId}/statistics/check-in")
    public ResponseEntity<CheckInStatisticsDTO> getCheckInStatistics(@PathVariable Long activityId) {
        return ResponseEntity.ok(participantService.getCheckInStatistics(activityId));
    }

    // 辅助方法
    private Sort.Order getSortOrder(String sort) {
        String[] parts = sort.split(",");
        String property = parts[0];
        String direction = parts.length > 1 ? parts[1] : "desc";
        return direction.equalsIgnoreCase("asc") 
                ? Sort.Order.asc(property) 
                : Sort.Order.desc(property);
    }
}