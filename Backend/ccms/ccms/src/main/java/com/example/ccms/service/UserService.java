package com.example.ccms.service;

import com.example.ccms.dto.request.LoginRequest;
import com.example.ccms.dto.request.RegisterRequest;
import com.example.ccms.dto.response.LoginResponse;

public interface UserService {
    // 用户注册
    void register(RegisterRequest request);

    // 用户登录（返回令牌）
    LoginResponse login(LoginRequest request);

    // 刷新令牌
    String refreshToken(String oldToken);

    // 退出登录（删除Redis中的令牌）
    void logout(String username);
}