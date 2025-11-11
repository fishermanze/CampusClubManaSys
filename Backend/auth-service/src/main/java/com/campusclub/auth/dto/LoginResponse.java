package com.campusclub.auth.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private UserInfo user;

    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String name;
        private String avatar;
        private String role;
        private String email;
        private String phone;
        private Integer joinedClubs;
        private Integer participatedActivities;
    }
}