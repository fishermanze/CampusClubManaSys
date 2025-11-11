package com.campusclub.activity.service;

import com.campusclub.activity.dto.ActivityCommentDTO;
import com.campusclub.activity.entity.ActivityComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ActivityCommentService {

    /**
     * 创建评论
     */
    ActivityComment createComment(ActivityComment comment);

    /**
     * 更新评论
     */
    ActivityComment updateComment(ActivityComment comment);

    /**
     * 删除评论
     */
    void deleteComment(Long commentId);

    /**
     * 点赞评论
     */
    void likeComment(Long commentId, Long userId);

    /**
     * 取消点赞评论
     */
    void unlikeComment(Long commentId, Long userId);

    /**
     * 获取评论详情
     */
    ActivityCommentDTO getCommentById(Long commentId, Long currentUserId);

    /**
     * 获取活动评论列表
     */
    Page<ActivityCommentDTO> getActivityComments(Long activityId, Pageable pageable, Long currentUserId);

    /**
     * 获取评论的回复
     */
    List<ActivityCommentDTO> getCommentReplies(Long commentId, Long currentUserId);

    /**
     * 获取用户发表的评论
     */
    Page<ActivityCommentDTO> getUserComments(Long userId, Pageable pageable);

    /**
     * 批量删除评论
     */
    void batchDeleteComments(List<Long> commentIds);

    /**
     * 统计活动评论数
     */
    long countActivityComments(Long activityId);

    /**
     * 检查用户是否点赞了评论
     */
    boolean isUserLikedComment(Long commentId, Long userId);

    /**
     * 审核评论
     */
    void auditComment(Long commentId, Integer status, String remark);

    /**
     * 搜索评论
     */
    Page<ActivityCommentDTO> searchComments(String keyword, Pageable pageable);

    /**
     * 获取热门评论
     */
    List<ActivityCommentDTO> getHotComments(Long activityId, int limit, Long currentUserId);
}