package com.campusclub.activity.repository;

import com.campusclub.activity.entity.ActivityComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityCommentRepository extends JpaRepository<ActivityComment, Long>, JpaSpecificationExecutor<ActivityComment> {

    /**
     * 查找活动的所有评论（按创建时间倒序）
     */
    List<ActivityComment> findByActivityIdAndParentIdIsNullOrderByCreatedAtDesc(Long activityId);

    /**
     * 查找评论的所有回复
     */
    List<ActivityComment> findByParentIdOrderByCreatedAtAsc(Long parentId);

    /**
     * 查找用户发表的所有评论
     */
    List<ActivityComment> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 点赞评论
     */
    @Modifying
    @Query("update ActivityComment c set c.likesCount = c.likesCount + 1 where c.id = ?1")
    void likeComment(Long commentId);

    /**
     * 取消点赞
     */
    @Modifying
    @Query("update ActivityComment c set c.likesCount = c.likesCount - 1 where c.id = ?1 and c.likesCount > 0")
    void unlikeComment(Long commentId);

    /**
     * 删除评论（软删除）
     */
    @Modifying
    @Query("update ActivityComment c set c.status = 1 where c.id = ?1")
    void softDeleteComment(Long commentId);

    /**
     * 统计活动评论数
     */
    long countByActivityIdAndStatusAndParentIdIsNull(Long activityId, Integer status);

    /**
     * 根据父评论ID统计回复数
     */
    long countByParentIdAndStatus(Long parentId, Integer status);
}