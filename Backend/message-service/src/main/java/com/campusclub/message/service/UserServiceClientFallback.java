package com.campusclub.message.service;

import org.springframework.stereotype.Component;

@Component
public class UserServiceClientFallback implements UserServiceClient {

    @Override
    public String getUserNameById(Long id) {
        return "用户" + id;
    }

    @Override
    public String getUserAvatarById(Long id) {
        return "/default-avatar.png";
    }
}