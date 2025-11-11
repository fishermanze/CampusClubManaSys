package com.campusclub.auth.controller;

import com.campusclub.auth.dto.LoginRequest;
import com.campusclub.auth.dto.LoginResponse;
import com.campusclub.auth.dto.RegisterRequest;
import com.campusclub.auth.dto.ApiResponse;
import com.campusclub.auth.entity.User;
import com.campusclub.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public ApiResponse<String> logout(HttpServletRequest request) {
        String token = extractToken(request);
        return userService.logout(token);
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh-token")
    public ApiResponse<LoginResponse> refreshToken(@RequestParam String refreshToken) {
        return userService.refreshToken(refreshToken);
    }

    /**
     * 发送验证码
     */
    @PostMapping("/send-code")
    public ApiResponse<String> sendVerificationCode(@RequestParam String phone) {
        return userService.sendVerificationCode(phone);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public ApiResponse<User> getProfile(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        // 从token中获取用户信息，这里简化处理，实际应该使用JWT工具类解析
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        User user = userService.getUserById(userId);
        if (user != null) {
            // 移除敏感信息
            user.setPassword(null);
            return ApiResponse.success(user);
        }
        return ApiResponse.error(404, "用户不存在");
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(
            HttpServletRequest request,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        return userService.changePassword(userId, oldPassword, newPassword, confirmPassword);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update-profile")
    public ApiResponse<User> updateProfile(HttpServletRequest request, @RequestBody User user) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        user.setId(userId); // 确保只能修改自己的信息
        return userService.updateUserInfo(user);
    }

    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(
            @RequestParam String phone,
            @RequestParam String verificationCode,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword) {
        return userService.resetPassword(phone, verificationCode, newPassword, confirmPassword);
    }

    /**
     * 从请求头中提取token
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}