package com.campusclub.activity.service.impl;

import com.campusclub.activity.dto.ActivityCommentDTO;
import com.campusclub.activity.entity.ActivityComment;
import com.campusclub.activity.repository.ActivityCommentRepository;
import com.campusclub.activity.service.ActivityCommentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityCommentServiceImpl implements ActivityCommentService {

    @Autowired
    private ActivityCommentRepository commentRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    @Transactional
    public ActivityComment createComment(ActivityComment comment) {
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public ActivityComment updateComment(ActivityComment comment) {
        ActivityComment existingComment = commentRepository.findById(comment.getId())
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        
        if (comment.getContent() != null) {
            existingComment.setContent(comment.getContent());
        }
        
        return commentRepository.save(existingComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        // 软删除评论
        commentRepository.softDeleteComment(commentId);
        
        // 可以考虑级联删除回复或标记回复为已删除
    }

    @Override
    @Transactional
    public void likeComment(Long commentId, Long userId) {
        commentRepository.likeComment(commentId);
        // 记录用户点赞记录
        System.out.println("用户" + userId + "点赞评论" + commentId);
    }

    @Override
    @Transactional
    public void unlikeComment(Long commentId, Long userId) {
        commentRepository.unlikeComment(commentId);
        // 删除用户点赞记录
        System.out.println("用户" + userId + "取消点赞评论" + commentId);
    }

    @Override
    public ActivityCommentDTO getCommentById(Long commentId, Long currentUserId) {
        ActivityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        return mapToDTO(comment, currentUserId, true);
    }

    @Override
    public Page<ActivityCommentDTO> getActivityComments(Long activityId, Pageable pageable, Long currentUserId) {
        // 只查询主评论（parentId为null）
        List<ActivityComment> comments = commentRepository.findByActivityIdAndParentIdIsNullOrderByCreatedAtDesc(activityId);
        
        // 简化实现，实际应使用Page
        List<ActivityCommentDTO> dtoList = new ArrayList<>();
        for (ActivityComment comment : comments) {
            dtoList.add(mapToDTO(comment, currentUserId, false));
        }
        
        return Page.empty();
    }

    @Override
    public List<ActivityCommentDTO> getCommentReplies(Long commentId, Long currentUserId) {
        List<ActivityComment> replies = commentRepository.findByParentIdOrderByCreatedAtAsc(commentId);
        List<ActivityCommentDTO> dtoList = new ArrayList<>();
        
        for (ActivityComment reply : replies) {
            dtoList.add(mapToDTO(reply, currentUserId, false));
        }
        
        return dtoList;
    }

    @Override
    public Page<ActivityCommentDTO> getUserComments(Long userId, Pageable pageable) {
        Page<ActivityComment> comments = commentRepository.findAll(
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId"), userId),
                pageable
        );
        
        return comments.map(comment -> modelMapper.map(comment, ActivityCommentDTO.class));
    }

    @Override
    @Transactional
    public void batchDeleteComments(List<Long> commentIds) {
        for (Long commentId : commentIds) {
            commentRepository.softDeleteComment(commentId);
        }
    }

    @Override
    public long countActivityComments(Long activityId) {
        return commentRepository.countByActivityIdAndStatusAndParentIdIsNull(activityId, 0);
    }

    @Override
    public boolean isUserLikedComment(Long commentId, Long userId) {
        // 需要查询点赞记录
        return false;
    }

    @Override
    @Transactional
    public void auditComment(Long commentId, Integer status, String remark) {
        ActivityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        comment.setStatus(status);
        commentRepository.save(comment);
    }

    @Override
    public Page<ActivityCommentDTO> searchComments(String keyword, Pageable pageable) {
        // 简化实现
        return Page.empty();
    }

    @Override
    public List<ActivityCommentDTO> getHotComments(Long activityId, int limit, Long currentUserId) {
        // 获取点赞数最多的评论
        List<ActivityComment> comments = commentRepository.findByActivityIdAndParentIdIsNullOrderByCreatedAtDesc(activityId);
        // 按点赞数排序并限制数量
        List<ActivityCommentDTO> dtoList = new ArrayList<>();
        
        for (ActivityComment comment : comments) {
            ActivityCommentDTO dto = mapToDTO(comment, currentUserId, false);
            dtoList.add(dto);
            
            if (dtoList.size() >= limit) {
                break;
            }
        }
        
        return dtoList;
    }

    /**
     * 安全映射实体为DTO，避免ModelMapper歧义和懒加载
     */
    private ActivityCommentDTO mapToDTO(ActivityComment comment, Long currentUserId, boolean includeReplies) {
        ActivityCommentDTO dto = new ActivityCommentDTO();
        dto.setId(comment.getId());
        dto.setActivityId(comment.getActivityId());
        dto.setUserId(comment.getUserId());
        dto.setContent(comment.getContent());
        dto.setParentId(comment.getParentId());
        dto.setLikesCount(comment.getLikesCount());
        dto.setStatus(comment.getStatus());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        dto.setIsLiked(isUserLikedComment(comment.getId(), currentUserId));
        // userName / userAvatar 可在聚合层补充或后续增强
        enrichUserInfo(dto);

        if (comment.getParentId() == null) {
            long replyCount = commentRepository.countByParentIdAndStatus(comment.getId(), 0);
            dto.setReplyCount((int) replyCount);
            if (includeReplies) {
                dto.setReplies(getCommentReplies(comment.getId(), currentUserId));
            }
        }
        return dto;
    }

    /**
     * 从 user-service 拉取用户名与头像，填充到 DTO
     */
    private void enrichUserInfo(ActivityCommentDTO dto) {
        if (dto.getUserId() == null) {
            return;
        }
        try {
            // 直接调用 user-service，避免通过网关的鉴权影响
            String url = "http://localhost:8082/users/" + dto.getUserId();
            System.out.println("正在获取用户信息: " + url);
            
            UserView userView = restTemplate.getForObject(url, UserView.class);
            if (userView != null) {
                System.out.println("获取到用户信息: ID=" + userView.getId() + 
                        ", username=" + userView.getUsername() + 
                        ", realName=" + userView.getRealName());
                
                // 优先使用 realName，如果没有则使用 username
                String name = Optional.ofNullable(userView.getRealName())
                        .filter(n -> !n.isEmpty())
                        .orElse(Optional.ofNullable(userView.getUsername())
                                .filter(n -> !n.isEmpty())
                                .orElse("匿名用户"));
                dto.setUserName(name);
                dto.setUserAvatar(userView.getAvatar());
                
                System.out.println("设置用户名: " + name);
            } else {
                System.err.println("获取用户信息失败: 用户ID " + dto.getUserId() + " 返回null");
            }
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // HTTP 错误（如 404, 401 等）
            System.err.println("获取用户信息HTTP错误: 用户ID " + dto.getUserId() + 
                    ", 状态码: " + e.getStatusCode() + 
                    ", 响应: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            // 其他错误
            System.err.println("获取用户信息失败: 用户ID " + dto.getUserId() + 
                    ", 错误类型: " + e.getClass().getName() + 
                    ", 错误信息: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 最小用户视图 - 匹配 UserDetailDTO 的字段
     * 使用 @JsonIgnoreProperties 忽略未知字段，确保能正确反序列化
     */
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserView {
        private Long id;
        private String username;
        private String realName;
        private String avatar;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
    }
}