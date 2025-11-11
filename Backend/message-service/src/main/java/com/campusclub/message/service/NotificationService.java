package com.campusclub.message.service;

import com.campusclub.message.dto.NotificationDTO;
import com.campusclub.message.entity.Notification;

import java.util.List;

public interface NotificationService {

    /**
     * 创建通知
     */
    NotificationDTO createNotification(Notification notification);

    /**
     * 批量创建通知
     */
    List<NotificationDTO> batchCreateNotifications(List<Notification> notifications);

    /**
     * 获取用户的通知列表
     */
    List<NotificationDTO> getUserNotifications(Long userId, Integer type, int page, int size);

    /**
     * 标记通知为已读
     */
    void markNotificationAsRead(Long notificationId, Long userId);

    /**
     * 标记所有通知为已读
     */
    void markAllNotificationsAsRead(Long userId);

    /**
     * 获取用户未读通知数量
     */
    long getUnreadNotificationCount(Long userId);

    /**
     * 删除通知
     */
    void deleteNotification(Long notificationId, Long userId);

    /**
     * 发送系统通知
     */
    NotificationDTO sendSystemNotification(Long userId, String title, String content, Long relatedId, String relatedType);
}