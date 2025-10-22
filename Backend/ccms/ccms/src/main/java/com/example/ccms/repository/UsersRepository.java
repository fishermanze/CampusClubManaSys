package com.example.ccms.repository;

import com.example.ccms.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    // 按用户名查询（登录用）
    Optional<Users> findByUsername(String username);

    // 检查用户名是否已存在（注册用）
    boolean existsByUsername(String username);
}
