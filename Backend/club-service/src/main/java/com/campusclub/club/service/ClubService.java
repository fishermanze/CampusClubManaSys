package com.campusclub.club.service;

import com.campusclub.club.entity.Club;
import com.campusclub.club.entity.ClubMember;

import java.util.List;
import java.util.Map;

public interface ClubService {
    // 社团管理相关
    Club createClub(Club club);
    Club updateClub(Club club);
    Club getClubById(Long id);
    List<Club> getAllClubs();
    List<Club> searchClubs(String keyword);
    void updateClubStatus(Long id, Integer status);
    List<Club> getClubsByCategory(String category);
    List<Club> getClubsByLeaderId(Long leaderId);

    // 社团成员管理相关
    ClubMember joinClub(Long clubId, Long userId);
    void leaveClub(Long clubId, Long userId);
    void updateMemberStatus(Long clubId, Long userId, Integer status);
    void updateMemberLevel(Long clubId, Long userId, Integer level);
    List<ClubMember> getClubMembers(Long clubId);
    List<ClubMember> getUserClubs(Long userId);
    void expelMember(Long clubId, Long userId);
    void evaluateMember(Long clubId, Long userId, Double score);
    Map<String, Object> getMemberYearEvaluation(Long clubId, Long userId);

    // 统计相关
    Integer getMemberCount(Long clubId);
    List<ClubMember> getTopActiveMembers(Long clubId, Integer limit);

    /**
     * 审批成员加入申请
     * @param clubId 社团ID
     * @param userId 申请用户ID
     * @param approved 是否通过
     * @param approverId 审批人ID（可为空）
     */
    void approveMember(Long clubId, Long userId, boolean approved, Long approverId);
}