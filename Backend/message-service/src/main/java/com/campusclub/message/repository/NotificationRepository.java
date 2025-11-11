package com.campusclub.message.repository;

import com.campusclub.message.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    /**
     * 获取用户的所有通知
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 获取用户的未读通知数量
     */
    long countByUserIdAndStatus(Long userId, Integer status);

    /**
     * 标记通知为已读
     */
    @Modifying
    @Query("update Notification n set n.status = 1 where n.id = ?1 and n.userId = ?2")
    void markAsRead(Long notificationId, Long userId);

    /**
     * 标记用户所有通知为已读
     */
    @Modifying
    @Query("update Notification n set n.status = 1 where n.userId = ?1")
    void markAllAsRead(Long userId);

    /**
     * 根据通知类型获取用户通知
     */
    List<Notification> findByUserIdAndNotificationTypeOrderByCreatedAtDesc(Long userId, Integer notificationType);

    /**
     * 根据关联ID和关联类型查找通知
     */
    List<Notification> findByRelatedIdAndRelatedType(Long relatedId, String relatedType);

    /**
     * 获取需要推送的未读通知
     */
    @Query("select n from Notification n where n.status = 0 and n.needPush = true")
    List<Notification> findPushableNotifications();
}