package com.campusclub.message.service.impl;

import com.campusclub.message.dto.NotificationDTO;
import com.campusclub.message.entity.Notification;
import com.campusclub.message.exception.BusinessException;
import com.campusclub.message.repository.NotificationRepository;
import com.campusclub.message.service.NotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public NotificationDTO createNotification(Notification notification) {
        Notification savedNotification = notificationRepository.save(notification);
        return modelMapper.map(savedNotification, NotificationDTO.class);
    }

    @Override
    @Transactional
    public List<NotificationDTO> batchCreateNotifications(List<Notification> notifications) {
        List<Notification> savedNotifications = notificationRepository.saveAll(notifications);
        return savedNotifications.stream()
                .map(notification -> modelMapper.map(notification, NotificationDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationDTO> getUserNotifications(Long userId, Integer type, int page, int size) {
        List<Notification> notifications;
        if (type != null) {
            notifications = notificationRepository.findByUserIdAndNotificationTypeOrderByCreatedAtDesc(userId, type);
        } else {
            notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }
        
        // 简单分页（实际项目中应该使用Spring Data的分页功能）
        int start = page * size;
        int end = Math.min(start + size, notifications.size());
        if (start >= notifications.size()) {
            return List.of();
        }
        
        return notifications.subList(start, end).stream()
                .map(notification -> modelMapper.map(notification, NotificationDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markNotificationAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException("通知不存在"));
        
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此通知");
        }
        
        notificationRepository.markAsRead(notificationId, userId);
    }

    @Override
    @Transactional
    public void markAllNotificationsAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    @Override
    public long getUnreadNotificationCount(Long userId) {
        return notificationRepository.countByUserIdAndStatus(userId, 0);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException("通知不存在"));
        
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此通知");
        }
        
        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public NotificationDTO sendSystemNotification(Long userId, String title, String content, Long relatedId, String relatedType) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setNotificationType(1); // 1-系统通知
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedId(relatedId);
        notification.setRelatedType(relatedType);
        notification.setNeedPush(true);
        notification.setStatus(0); // 0-未读
        
        return createNotification(notification);
    }
}