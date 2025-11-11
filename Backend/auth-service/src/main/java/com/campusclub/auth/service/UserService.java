package com.campusclub.auth.service;

import com.campusclub.auth.dto.LoginRequest;
import com.campusclub.auth.dto.LoginResponse;
import com.campusclub.auth.dto.RegisterRequest;
import com.campusclub.auth.dto.ApiResponse;
import com.campusclub.auth.entity.User;

public interface UserService {
    /**
     * 用户登录
     */
    ApiResponse<LoginResponse> login(LoginRequest request);

    /**
     * 用户注册
     */
    ApiResponse<String> register(RegisterRequest request);

    /**
     * 退出登录
     */
    ApiResponse<String> logout(String token);

    /**
     * 刷新令牌
     */
    ApiResponse<LoginResponse> refreshToken(String refreshToken);

    /**
     * 发送验证码
     */
    ApiResponse<String> sendVerificationCode(String phone);

    /**
     * 根据用户名获取用户信息
     */
    User getUserByUsername(String username);

    /**
     * 根据ID获取用户信息
     */
    User getUserById(Long id);

    /**
     * 修改用户密码
     */
    ApiResponse<String> changePassword(Long userId, String oldPassword, String newPassword, String confirmPassword);

    /**
     * 更新用户信息
     */
    ApiResponse<User> updateUserInfo(User user);

    /**
     * 重置密码
     */
    ApiResponse<String> resetPassword(String phone, String verificationCode, String newPassword, String confirmPassword);
}