package com.example.ccms.controller;

import com.example.ccms.dto.request.LoginRequest;
import com.example.ccms.dto.request.RegisterRequest;
import com.example.ccms.dto.response.LoginResponse;
import com.example.ccms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // 用户注册
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok("注册成功");
    }

    // 用户登录
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    // 刷新令牌
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(@RequestParam String oldToken) {
        String newToken = userService.refreshToken(oldToken);
        return ResponseEntity.ok(newToken);
    }

    // 退出登录
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String username) {
        userService.logout(username);
        return ResponseEntity.ok("退出成功");
    }
}