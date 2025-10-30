package com.example.ccms.repository;

import com.example.ccms.entity.ClubApplication;
import com.example.ccms.enums.ApplicationStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubApplicationRepository extends JpaRepository<ClubApplication, Long> {
    boolean existsByUserIdAndClubId(Long userId, Long clubId);
    List<ClubApplication> findByClubIdAndStatus(Long clubId, ApplicationStatusEnum status);
    Optional<ClubApplication> findByIdAndClubId(Long id, Long clubId);
}