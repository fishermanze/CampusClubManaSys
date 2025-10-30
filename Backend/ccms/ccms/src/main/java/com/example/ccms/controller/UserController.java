package com.example.ccms.controller;

import com.example.ccms.dto.request.PasswordChangeRequest;
import com.example.ccms.dto.response.ApiResponse;
import com.example.ccms.dto.response.UserVO;
import com.example.ccms.security.CurrentUser;
import com.example.ccms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<UserVO>> getUserInfo(@CurrentUser Long userId) {
        UserVO userVO = userService.getUserInfo(userId).getData();
        return ResponseEntity.ok(ApiResponse.success("查询成功", userVO));
    }

    @PutMapping("/info")
    public ResponseEntity<ApiResponse<Void>> updateUserInfo(
            @CurrentUser Long userId,
            @Valid @RequestBody UserVO userVO) {
        userService.updateUserInfo(userId, userVO);
        return ResponseEntity.ok(ApiResponse.success("更新成功"));
    }

    @PostMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @CurrentUser Long userId,
            @Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success("密码修改成功"));
    }
}