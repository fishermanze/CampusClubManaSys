package com.campusclub.user.repository;

import com.campusclub.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    /**
     * 根据用户ID查找用户档案
     */
    Optional<UserProfile> findByUser_Id(Long userId);

    /**
     * 根据学号查找用户档案
     */
    Optional<UserProfile> findByStudentId(String studentId);

    /**
     * 检查用户ID是否已存在
     */
    boolean existsByUser_Id(Long userId);

    /**
     * 检查学号是否已存在
     */
    boolean existsByStudentId(String studentId);
}