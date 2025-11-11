package com.campusclub.user.repository;

import com.campusclub.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据手机号查找用户
     */
    Optional<User> findByPhone(String phone);

    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据角色查找用户
     */
    List<User> findByRole(String role);

    /**
     * 根据状态查找用户
     */
    List<User> findByStatus(String status);

    /**
     * 查询用户名或真实姓名包含关键字的用户
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.realName LIKE %:keyword%")
    List<User> searchByKeyword(String keyword);

    /**
     * 检查用户名是否已存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查手机号是否已存在
     */
    boolean existsByPhone(String phone);

    /**
     * 检查邮箱是否已存在
     */
    boolean existsByEmail(String email);

    /**
     * 根据ID列表查询用户，支持分页
     */
    Page<User> findByIdIn(List<Long> ids, Pageable pageable);
}