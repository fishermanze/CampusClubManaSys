package com.example.ccms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token; // JWT令牌
    private Integer userId; // 用户ID
    private String username; // 用户名
    private String role; // 权限角色
}