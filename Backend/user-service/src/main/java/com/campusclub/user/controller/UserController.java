package com.campusclub.user.controller;

import com.campusclub.user.dto.UserDTO;
import com.campusclub.user.dto.UserProfileDTO;
import com.campusclub.user.dto.UserDetailDTO;
import com.campusclub.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户列表（管理员）
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<UserDTO> userPage = userService.getUserList(pageable, keyword, role, status);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", userPage.getContent());
        response.put("totalElements", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());
        response.put("currentPage", page);
        response.put("size", size);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDetailDTO> getUserDetail(@PathVariable Long id, HttpServletRequest request) {
        // 从请求头获取当前用户ID
        Long currentUserId = null;
        try {
            String userIdStr = request.getHeader("X-User-Id");
            if (userIdStr != null) {
                currentUserId = Long.parseLong(userIdStr);
            }
        } catch (NumberFormatException e) {
            // 忽略异常，currentUserId保持为null
        }
        
        UserDetailDTO userDetail = userService.getUserDetailById(id, currentUserId);
        return ResponseEntity.ok(userDetail);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<UserDetailDTO> getCurrentUser(HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        UserDetailDTO userDetail = userService.getUserDetailById(userId, userId);
        return ResponseEntity.ok(userDetail);
    }

    /**
     * 更新用户基本信息
     */
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateCurrentUser(@RequestBody UserDTO userDTO, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        UserDTO updatedUser = userService.updateUserInfo(userId, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 更新用户档案
     */
    @PutMapping("/me/profile")
    public ResponseEntity<UserProfileDTO> updateUserProfile(@RequestBody UserProfileDTO profileDTO, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        UserProfileDTO updatedProfile = userService.updateUserProfile(userId, profileDTO);
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * 上传用户头像
     */
    @PostMapping("/me/avatar")
    public ResponseEntity<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        String avatarUrl = userService.uploadAvatar(userId, file);
        
        Map<String, String> response = new HashMap<>();
        response.put("avatarUrl", avatarUrl);
        return ResponseEntity.ok(response);
    }

    /**
     * 关注用户
     */
    @PostMapping("/{id}/follow")
    public ResponseEntity<Map<String, Boolean>> followUser(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = Long.parseLong(request.getHeader("X-User-Id"));
        boolean success = userService.followUser(currentUserId, id);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }

    /**
     * 取消关注
     */
    @DeleteMapping("/{id}/follow")
    public ResponseEntity<Map<String, Boolean>> unfollowUser(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = Long.parseLong(request.getHeader("X-User-Id"));
        boolean success = userService.unfollowUser(currentUserId, id);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", success);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户的关注列表
     */
    @GetMapping("/{id}/followings")
    public ResponseEntity<Map<String, Object>> getUserFollowings(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<UserDTO> followingsPage = userService.getUserFollowings(id, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", followingsPage.getContent());
        response.put("totalElements", followingsPage.getTotalElements());
        response.put("totalPages", followingsPage.getTotalPages());
        response.put("currentPage", page);
        response.put("size", size);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户的粉丝列表
     */
    @GetMapping("/{id}/followers")
    public ResponseEntity<Map<String, Object>> getUserFollowers(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<UserDTO> followersPage = userService.getUserFollowers(id, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", followersPage.getContent());
        response.put("totalElements", followersPage.getTotalElements());
        response.put("totalPages", followersPage.getTotalPages());
        response.put("currentPage", page);
        response.put("size", size);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取两个用户的共同关注
     */
    @GetMapping("/common-followings")
    public ResponseEntity<List<UserDTO>> getCommonFollowings(
            @RequestParam Long userId1,
            HttpServletRequest request) {
        
        Long userId2 = Long.parseLong(request.getHeader("X-User-Id"));
        List<UserDTO> commonFollowings = userService.getCommonFollowings(userId1, userId2);
        return ResponseEntity.ok(commonFollowings);
    }

    /**
     * 批量获取用户信息（用于远程调用）
     */
    @GetMapping("/batch")
    public ResponseEntity<List<UserDTO>> getUsersByIds(@RequestParam List<Long> ids) {
        List<UserDTO> users = userService.getUsersByIds(ids);
        return ResponseEntity.ok(users);
    }

    /**
     * 根据用户名获取用户信息（用于远程调用）
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        UserDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    /**
     * 更新用户状态（管理员）
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<UserDTO> updateUserStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        UserDTO updatedUser = userService.updateUserStatus(id, status);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 检查是否关注了用户
     */
    @GetMapping("/{id}/is-following")
    public ResponseEntity<Map<String, Boolean>> isFollowing(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = Long.parseLong(request.getHeader("X-User-Id"));
        boolean isFollowing = userService.isFollowing(currentUserId, id);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("isFollowing", isFollowing);
        return ResponseEntity.ok(response);
    }
}