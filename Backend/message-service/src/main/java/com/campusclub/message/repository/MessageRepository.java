package com.campusclub.message.repository;

import com.campusclub.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {

    /**
     * 获取两个用户之间的聊天记录
     */
    List<Message> findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByCreatedAtDesc(
            Long senderId1, Long receiverId1, Long senderId2, Long receiverId2);

    /**
     * 获取用户的所有消息（按创建时间倒序）
     */
    List<Message> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    /**
     * 获取用户的未读消息数量
     */
    long countByReceiverIdAndStatus(Long receiverId, Integer status);

    /**
     * 将消息标记为已读
     */
    @Modifying
    @Query("update Message m set m.status = 1 where m.receiverId = ?1 and m.senderId = ?2")
    void markMessagesAsRead(Long receiverId, Long senderId);

    /**
     * 获取最新的消息（用于会话列表展示）
     */
    @Query("select m from Message m where (m.senderId = ?1 or m.receiverId = ?1) order by m.createdAt desc limit 1")
    Message findLatestMessageByUserId(Long userId);

    /**
     * 删除用户的消息（软删除）
     */
    @Modifying
    @Query("update Message m set m.status = 2 where m.id = ?1 and (m.senderId = ?2 or m.receiverId = ?2)")
    void softDeleteMessage(Long messageId, Long userId);

    /**
     * 根据关联ID和关联类型查找消息
     */
    List<Message> findByRelatedIdAndRelatedType(Long relatedId, String relatedType);
}