package com.campusclub.user.service;

import com.campusclub.user.dto.UserDTO;
import com.campusclub.user.dto.UserProfileDTO;
import com.campusclub.user.dto.UserDetailDTO;
import com.campusclub.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    /**
     * 获取用户列表
     */
    Page<UserDTO> getUserList(Pageable pageable, String keyword, String role, String status);

    /**
     * 根据ID获取用户详情
     */
    UserDetailDTO getUserDetailById(Long id, Long currentUserId);

    /**
     * 更新用户信息
     */
    UserDTO updateUserInfo(Long id, UserDTO userDTO);

    /**
     * 更新用户档案
     */
    UserProfileDTO updateUserProfile(Long userId, UserProfileDTO profileDTO);

    /**
     * 更新用户状态
     */
    UserDTO updateUserStatus(Long id, String status);

    /**
     * 上传用户头像
     */
    String uploadAvatar(Long userId, MultipartFile file);

    /**
     * 关注用户
     */
    boolean followUser(Long followerId, Long followedId);

    /**
     * 取消关注
     */
    boolean unfollowUser(Long followerId, Long followedId);

    /**
     * 获取用户的关注列表
     */
    Page<UserDTO> getUserFollowings(Long userId, Pageable pageable);

    /**
     * 获取用户的粉丝列表
     */
    Page<UserDTO> getUserFollowers(Long userId, Pageable pageable);

    /**
     * 检查用户是否关注了指定用户
     */
    boolean isFollowing(Long followerId, Long followedId);

    /**
     * 获取两个用户的共同关注
     */
    List<UserDTO> getCommonFollowings(Long userId1, Long userId2);

    /**
     * 根据用户名获取用户信息（用于远程调用）
     */
    UserDTO getUserByUsername(String username);

    /**
     * 根据用户ID列表批量获取用户信息
     */
    List<UserDTO> getUsersByIds(List<Long> ids);

    /**
     * 更新用户登录信息
     */
    void updateLoginInfo(Long userId);
}