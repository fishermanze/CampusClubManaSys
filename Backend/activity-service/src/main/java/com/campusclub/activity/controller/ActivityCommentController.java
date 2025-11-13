package com.campusclub.activity.controller;

import com.campusclub.activity.dto.ActivityCommentDTO;
import com.campusclub.activity.entity.ActivityComment;
import com.campusclub.activity.service.ActivityCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/comments")
public class ActivityCommentController {

    @Autowired
    private ActivityCommentService commentService;

    /**
     * 创建评论
     */
    @PostMapping
    public ResponseEntity<ActivityComment> createComment(@RequestBody ActivityComment comment, HttpServletRequest request) {
        // 如果请求头中有用户ID，优先使用请求头中的（更安全）
        Long currentUserId = getCurrentUserId(request);
        if (currentUserId != null) {
            comment.setUserId(currentUserId);
        }
        // 如果请求头中没有用户ID，使用请求体中的userId（兼容旧版本）
        if (comment.getUserId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        return ResponseEntity.ok(commentService.createComment(comment));
    }

    /**
     * 更新评论
     */
    @PutMapping("/{id}")
    public ResponseEntity<ActivityComment> updateComment(@PathVariable Long id, @RequestBody ActivityComment comment) {
        comment.setId(id);
        return ResponseEntity.ok(commentService.updateComment(comment));
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 点赞评论
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeComment(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        commentService.likeComment(id, currentUserId);
        return ResponseEntity.ok().build();
    }

    /**
     * 取消点赞评论
     */
    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> unlikeComment(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        commentService.unlikeComment(id, currentUserId);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取评论详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ActivityCommentDTO> getCommentById(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        return ResponseEntity.ok(commentService.getCommentById(id, currentUserId));
    }

    /**
     * 获取活动评论列表
     */
    @GetMapping("/activity/{activityId}")
    public ResponseEntity<Page<ActivityCommentDTO>> getActivityComments(
            @PathVariable Long activityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            HttpServletRequest request) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(getSortOrder(sort)));
        Long currentUserId = getCurrentUserId(request);
        return ResponseEntity.ok(commentService.getActivityComments(activityId, pageable, currentUserId));
    }

    /**
     * 获取评论的回复
     */
    @GetMapping("/{id}/replies")
    public ResponseEntity<List<ActivityCommentDTO>> getCommentReplies(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        return ResponseEntity.ok(commentService.getCommentReplies(id, currentUserId));
    }

    /**
     * 获取用户发表的评论
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ActivityCommentDTO>> getUserComments(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(getSortOrder(sort)));
        return ResponseEntity.ok(commentService.getUserComments(userId, pageable));
    }

    /**
     * 批量删除评论
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDeleteComments(@RequestBody List<Long> commentIds) {
        commentService.batchDeleteComments(commentIds);
        return ResponseEntity.noContent().build();
    }

    /**
     * 统计活动评论数
     */
    @GetMapping("/activity/{activityId}/count")
    public ResponseEntity<Long> countActivityComments(@PathVariable Long activityId) {
        return ResponseEntity.ok(commentService.countActivityComments(activityId));
    }

    /**
     * 检查用户是否点赞了评论
     */
    @GetMapping("/{id}/like/user/{userId}")
    public ResponseEntity<Boolean> isUserLikedComment(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(commentService.isUserLikedComment(id, userId));
    }

    /**
     * 审核评论
     */
    @PutMapping("/{id}/audit")
    public ResponseEntity<Void> auditComment(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String remark) {
        
        commentService.auditComment(id, status, remark);
        return ResponseEntity.ok().build();
    }

    /**
     * 搜索评论
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ActivityCommentDTO>> searchComments(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(commentService.searchComments(keyword, pageable));
    }

    /**
     * 获取热门评论
     */
    @GetMapping("/activity/{activityId}/hot")
    public ResponseEntity<List<ActivityCommentDTO>> getHotComments(
            @PathVariable Long activityId,
            @RequestParam(defaultValue = "5") int limit,
            HttpServletRequest request) {
        
        Long currentUserId = getCurrentUserId(request);
        return ResponseEntity.ok(commentService.getHotComments(activityId, limit, currentUserId));
    }

    // 辅助方法 - 从请求头中获取用户ID
    private Long getCurrentUserId(HttpServletRequest request) {
        if (request != null) {
            // 尝试从请求头中获取用户ID（API Gateway可能传递的header名称）
            String userIdHeader = request.getHeader("X-User-Id");
            if (userIdHeader == null || userIdHeader.isEmpty()) {
                userIdHeader = request.getHeader("UserId");
            }
            if (userIdHeader == null || userIdHeader.isEmpty()) {
                userIdHeader = request.getHeader("user-id");
            }
            
            if (userIdHeader != null && !userIdHeader.isEmpty()) {
                try {
                    return Long.parseLong(userIdHeader);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }

    private Sort.Order getSortOrder(String sort) {
        if (sort == null || sort.isEmpty()) {
            return Sort.Order.desc("createdAt");
        }
        String[] parts = sort.split(",");
        String property = (parts.length > 0 && parts[0] != null && !parts[0].isEmpty()) 
                ? parts[0] : "createdAt";
        String direction = (parts.length > 1 && parts[1] != null && !parts[1].isEmpty()) 
                ? parts[1] : "desc";
        return "asc".equalsIgnoreCase(direction) 
                ? Sort.Order.asc(property) 
                : Sort.Order.desc(property);
    }
}