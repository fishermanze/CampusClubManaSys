// src/main/java/com/club/auth/service/UserService.java
package com.club.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.club.common.entity.User;

public interface UserService extends IService<User> {
    // 根据用户名查询用户
    User getByUsername(String username);
}