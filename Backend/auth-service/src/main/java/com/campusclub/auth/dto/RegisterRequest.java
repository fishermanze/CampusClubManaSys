package com.campusclub.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String confirmPassword;
    private String realName;
    private String uid;
    private String phone;
    private String verificationCode;
}