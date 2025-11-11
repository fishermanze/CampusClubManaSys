package com.campusclub.club.repository;

import com.campusclub.club.entity.ClubMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {
    List<ClubMember> findByClubId(Long clubId);
    List<ClubMember> findByUserId(Long userId);
    Optional<ClubMember> findByClubIdAndUserId(Long clubId, Long userId);
    List<ClubMember> findByClubIdAndRole(Long clubId, Integer role);
    List<ClubMember> findByClubIdAndStatus(Long clubId, Integer status);
    List<ClubMember> findByUserIdAndRole(Long userId, Integer role);
    List<ClubMember> findByUserIdAndRoleIn(Long userId, List<Integer> roles);
}