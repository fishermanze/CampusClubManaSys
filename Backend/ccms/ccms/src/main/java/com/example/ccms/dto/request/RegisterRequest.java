package com.example.ccms.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String uid; // 校园统一标识
    private String realName;
}