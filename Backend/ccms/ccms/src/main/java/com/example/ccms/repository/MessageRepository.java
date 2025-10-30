package com.example.ccms.repository;

import com.example.ccms.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * 分页查询用户的所有消息（按创建时间倒序）
     * @param userId 用户ID
     * @param pageable 分页参数（页码、每页条数、排序规则）
     * @return 分页后的消息列表
     */
    Page<Message> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);

    /**
     * 查询用户未读消息的数量
     * @param userId 用户ID
     * @return 未读消息数量
     */
    Long countByUserIdAndIsReadFalse(Long userId);

    /**
     * 批量将用户的未读消息标记为已读
     * @param userId 用户ID
     */
    void markAllByUserIdAndIsReadFalseAsRead(Long userId);
}