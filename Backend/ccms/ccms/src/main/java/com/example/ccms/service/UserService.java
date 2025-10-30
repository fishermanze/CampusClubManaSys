package com.example.ccms.service;

import com.example.ccms.dto.request.LoginRequest;
import com.example.ccms.dto.request.RegisterRequest;
import com.example.ccms.dto.response.ApiResponse;
import com.example.ccms.dto.response.LoginResponse;
import com.example.ccms.dto.response.UserVO;
import com.example.ccms.dto.request.PasswordChangeRequest;

public interface UserService {
    ApiResponse<LoginResponse> login(LoginRequest request);
    ApiResponse<Void> register(RegisterRequest request);
    ApiResponse<UserVO> getUserInfo(Long userId);
    ApiResponse<Void> updateUserInfo(Long userId, UserVO userVO);
    ApiResponse<Void> changePassword(Long userId, PasswordChangeRequest request);
}