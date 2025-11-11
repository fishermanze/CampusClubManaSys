package com.campusclub.message.controller;

import com.campusclub.message.dto.NotificationDTO;
import com.campusclub.message.entity.Notification;
import com.campusclub.message.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 创建通知
     */
    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(@RequestBody Notification notification) {
        NotificationDTO notificationDTO = notificationService.createNotification(notification);
        return ResponseEntity.ok(notificationDTO);
    }

    /**
     * 批量创建通知
     */
    @PostMapping("/batch")
    public ResponseEntity<List<NotificationDTO>> batchCreateNotifications(@RequestBody List<Notification> notifications) {
        List<NotificationDTO> notificationDTOs = notificationService.batchCreateNotifications(notifications);
        return ResponseEntity.ok(notificationDTOs);
    }

    /**
     * 获取用户的通知列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<NotificationDTO> notifications = notificationService.getUserNotifications(userId, type, page, size);
        return ResponseEntity.ok(notifications);
    }

    /**
     * 标记通知为已读
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(
            @PathVariable Long notificationId,
            @RequestParam Long userId) {
        notificationService.markNotificationAsRead(notificationId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 标记所有通知为已读
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead(@PathVariable Long userId) {
        notificationService.markAllNotificationsAsRead(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取用户未读通知数量
     */
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadNotificationCount(@PathVariable Long userId) {
        long count = notificationService.getUnreadNotificationCount(userId);
        return ResponseEntity.ok(count);
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long notificationId,
            @RequestParam Long userId) {
        notificationService.deleteNotification(notificationId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 发送系统通知
     */
    @PostMapping("/system")
    public ResponseEntity<NotificationDTO> sendSystemNotification(
            @RequestParam Long userId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) Long relatedId,
            @RequestParam(required = false) String relatedType) {
        NotificationDTO notificationDTO = notificationService.sendSystemNotification(
                userId, title, content, relatedId, relatedType);
        return ResponseEntity.ok(notificationDTO);
    }
}