package com.campusclub.message.repository;

import com.campusclub.message.entity.MessageReadRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageReadRecordRepository extends JpaRepository<MessageReadRecord, Long>, JpaSpecificationExecutor<MessageReadRecord> {

    /**
     * 根据用户ID和对方用户ID查找阅读记录
     */
    Optional<MessageReadRecord> findByUserIdAndOtherUserId(Long userId, Long otherUserId);

    /**
     * 获取用户的所有会话记录
     */
    List<MessageReadRecord> findByUserIdOrderByLastReadTimeDesc(Long userId);

    /**
     * 更新未读消息数量
     */
    @Modifying
    @Query("update MessageReadRecord m set m.unreadCount = m.unreadCount + 1 where m.userId = ?1 and m.otherUserId = ?2")
    void incrementUnreadCount(Long userId, Long otherUserId);

    /**
     * 重置未读消息数量
     */
    @Modifying
    @Query("update MessageReadRecord m set m.unreadCount = 0, m.lastReadTime = ?3, m.lastReadMessageId = ?4 where m.userId = ?1 and m.otherUserId = ?2")
    void resetUnreadCount(Long userId, Long otherUserId, LocalDateTime lastReadTime, Long lastReadMessageId);

    /**
     * 更新置顶状态
     */
    @Modifying
    @Query("update MessageReadRecord m set m.isSticky = ?3 where m.userId = ?1 and m.otherUserId = ?2")
    void updateStickyStatus(Long userId, Long otherUserId, Boolean isSticky);

    /**
     * 更新免打扰状态
     */
    @Modifying
    @Query("update MessageReadRecord m set m.isMuted = ?3 where m.userId = ?1 and m.otherUserId = ?2")
    void updateMuteStatus(Long userId, Long otherUserId, Boolean isMuted);

    /**
     * 获取用户未读消息总数
     */
    @Query("select sum(m.unreadCount) from MessageReadRecord m where m.userId = ?1")
    Long getTotalUnreadCount(Long userId);
}