package com.campusclub.message.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", fallback = UserServiceClientFallback.class)
public interface UserServiceClient {

    /**
     * 根据用户ID获取用户名
     */
    @GetMapping("/api/users/{id}/name")
    String getUserNameById(@PathVariable("id") Long id);

    /**
     * 根据用户ID获取用户头像
     */
    @GetMapping("/api/users/{id}/avatar")
    String getUserAvatarById(@PathVariable("id") Long id);
}