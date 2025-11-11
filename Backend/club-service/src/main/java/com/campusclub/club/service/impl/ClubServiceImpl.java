package com.campusclub.club.service.impl;

import com.campusclub.club.entity.Club;
import com.campusclub.club.entity.ClubMember;
import com.campusclub.club.repository.ClubRepository;
import com.campusclub.club.repository.ClubMemberRepository;
import com.campusclub.club.service.ClubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ClubServiceImpl implements ClubService {

    private static final Logger log = LoggerFactory.getLogger(ClubServiceImpl.class);

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gateway.base-url:http://localhost:8080}")
    private String gatewayBaseUrl;

    @Override
    @Transactional
    public Club createClub(Club club) {
        club.setCreateTime(new Date());
        club.setUpdateTime(new Date());
        club.setStatus(0); // 待审核
        club.setMemberCount(0);
        club.setTotalActivityCount(0);
        return clubRepository.save(club);
    }

    @Override
    @Transactional
    public Club updateClub(Club club) {
        club.setUpdateTime(new Date());
        return clubRepository.save(club);
    }

    @Override
    public Club getClubById(Long id) {
        return clubRepository.findById(id).orElse(null);
    }

    @Override
    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    @Override
    public List<Club> searchClubs(String keyword) {
        return clubRepository.findByNameContaining(keyword);
    }

    @Override
    @Transactional
    public void updateClubStatus(Long id, Integer status) {
        Club club = clubRepository.findById(id).orElseThrow();
        club.setStatus(status);
        clubRepository.save(club);
    }

    @Override
    public List<Club> getClubsByCategory(String category) {
        return clubRepository.findByCategory(category);
    }

    @Override
    public List<Club> getClubsByLeaderId(Long leaderId) {
        // 1) 直接匹配 Club.leaderId
        List<Club> direct = clubRepository.findByLeaderId(leaderId);
        // 2) 通过 ClubMember 中 role=1(负责人) 反查
        List<ClubMember> managed = clubMemberRepository.findByUserIdAndRoleIn(leaderId, Arrays.asList(1, 2));
        Set<Long> clubIds = new HashSet<>();
        for (Club c : direct) {
            clubIds.add(c.getId());
        }
        for (ClubMember cm : managed) {
            if (cm.getClubId() != null) {
                clubIds.add(cm.getClubId());
            }
        }
        if (clubIds.isEmpty()) {
            return direct;
        }
        List<Club> merged = new ArrayList<>();
        for (Long cid : clubIds) {
            clubRepository.findById(cid).ifPresent(merged::add);
        }
        return merged;
    }

    @Override
    @Transactional
    public ClubMember joinClub(Long clubId, Long userId) {
        ClubMember member = new ClubMember();
        member.setClubId(clubId);
        member.setUserId(userId);
        member.setRole(0); // 普通成员
        member.setStatus(0); // 待审核
        member.setJoinTime(new Date());
        member.setUpdateTime(new Date());
        member.setActivityCount(0);
        member.setTotalScore(0.0);
        member.setLevel(1); // 初始职级
        member.setYearEvaluationStatus(0);
        ClubMember saved = clubMemberRepository.save(member);
        // 仅通知该社团负责人
        try {
            Club club = clubRepository.findById(clubId).orElse(null);
            if (club != null && club.getLeaderId() != null && !club.getLeaderId().equals(userId)) {
                Map<String, Object> payload = new HashMap<>();
                payload.put("userId", club.getLeaderId());
                payload.put("notificationType", 2); // 社团通知
                payload.put("title", "新的入团申请待审核");
                payload.put("content", "有新的入团申请需要处理。");
                payload.put("relatedId", saved.getId());
                payload.put("relatedType", "club_join_review");
                restTemplate.postForEntity(gatewayBaseUrl + "/notifications", payload, Map.class);
            }
        } catch (Exception ignored) {
            // 通知失败不影响主流程
        }
        return saved;
    }

    @Override
    @Transactional
    public void leaveClub(Long clubId, Long userId) {
        ClubMember member = clubMemberRepository.findByClubIdAndUserId(clubId, userId)
                .orElseThrow();
        member.setStatus(2); // 已退出
        member.setUpdateTime(new Date());
        clubMemberRepository.save(member);
    }

    @Override
    @Transactional
    public void updateMemberStatus(Long clubId, Long userId, Integer status) {
        ClubMember member = clubMemberRepository.findByClubIdAndUserId(clubId, userId)
                .orElseThrow();
        member.setStatus(status);
        member.setUpdateTime(new Date());
        clubMemberRepository.save(member);
    }

    @Override
    @Transactional
    public void updateMemberLevel(Long clubId, Long userId, Integer level) {
        ClubMember member = clubMemberRepository.findByClubIdAndUserId(clubId, userId)
                .orElseThrow();
        member.setLevel(level);
        member.setUpdateTime(new Date());
        clubMemberRepository.save(member);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ClubMember> getClubMembers(Long clubId) {
        List<ClubMember> members = clubMemberRepository.findByClubId(clubId);
        
        // 对于信息不完整的成员，从 user-service 获取用户详细信息并补充
        for (ClubMember member : members) {
            if (member.getUserId() != null && needsUserInfoSupplement(member)) {
                try {
                    // 从 user-service 获取用户详细信息
                    String userServiceUrl = gatewayBaseUrl + "/user/" + member.getUserId();
                    Map<String, Object> userDetail = restTemplate.getForObject(userServiceUrl, Map.class);
                    
                    if (userDetail != null) {
                        supplementMemberInfo(member, userDetail);
                        log.debug("Supplemented user info for userId: {}", member.getUserId());
                    }
                } catch (Exception e) {
                    // 如果获取用户信息失败，记录日志但继续处理其他成员
                    log.warn("Failed to fetch user detail for userId: {}, error: {}", 
                            member.getUserId(), e.getMessage());
                }
            }
        }
        
        return members;
    }
    
    /**
     * 检查成员是否需要补充用户信息
     */
    private boolean needsUserInfoSupplement(ClubMember member) {
        return (member.getRealName() == null || member.getRealName().isEmpty() ||
                member.getGender() == null || member.getGender().isEmpty() ||
                member.getMajor() == null || member.getMajor().isEmpty() ||
                member.getClassName() == null || member.getClassName().isEmpty());
    }
    
    /**
     * 从用户详细信息中补充成员信息
     */
    private void supplementMemberInfo(ClubMember member, Map<String, Object> userDetail) {
        // 补充缺失的信息
        if ((member.getRealName() == null || member.getRealName().isEmpty()) && userDetail.get("realName") != null) {
            member.setRealName(String.valueOf(userDetail.get("realName")));
        }
        if ((member.getGender() == null || member.getGender().isEmpty()) && userDetail.get("gender") != null) {
            member.setGender(String.valueOf(userDetail.get("gender")));
        }
        if ((member.getMajor() == null || member.getMajor().isEmpty()) && userDetail.get("major") != null) {
            member.setMajor(String.valueOf(userDetail.get("major")));
        }
        if ((member.getClassName() == null || member.getClassName().isEmpty()) && userDetail.get("className") != null) {
            member.setClassName(String.valueOf(userDetail.get("className")));
        }
    }

    @Override
    public List<ClubMember> getUserClubs(Long userId) {
        return clubMemberRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void expelMember(Long clubId, Long userId) {
        ClubMember member = clubMemberRepository.findByClubIdAndUserId(clubId, userId)
                .orElseThrow();
        member.setStatus(3); // 已开除
        member.setUpdateTime(new Date());
        clubMemberRepository.save(member);
    }

    @Override
    @Transactional
    public void evaluateMember(Long clubId, Long userId, Double score) {
        ClubMember member = clubMemberRepository.findByClubIdAndUserId(clubId, userId)
                .orElseThrow();
        member.setTotalScore(member.getTotalScore() + score);
        member.setUpdateTime(new Date());
        clubMemberRepository.save(member);
    }

    @Override
    public Map<String, Object> getMemberYearEvaluation(Long clubId, Long userId) {
        ClubMember member = clubMemberRepository.findByClubIdAndUserId(clubId, userId)
                .orElseThrow();
        Map<String, Object> evaluation = new HashMap<>();
        evaluation.put("memberId", member.getId());
        evaluation.put("userId", member.getUserId());
        evaluation.put("activityCount", member.getActivityCount());
        evaluation.put("totalScore", member.getTotalScore());
        evaluation.put("level", member.getLevel());
        return evaluation;
    }

    @Override
    public Integer getMemberCount(Long clubId) {
        return clubMemberRepository.findByClubIdAndStatus(clubId, 1).size();
    }

    @Override
    public List<ClubMember> getTopActiveMembers(Long clubId, Integer limit) {
        List<ClubMember> members = clubMemberRepository.findByClubId(clubId);
        members.sort((a, b) -> b.getActivityCount().compareTo(a.getActivityCount()));
        return members.subList(0, Math.min(limit, members.size()));
    }

    @Override
    @Transactional
    public void approveMember(Long clubId, Long userId, boolean approved, Long approverId) {
        ClubMember member = clubMemberRepository.findByClubIdAndUserId(clubId, userId)
                .orElseThrow();
        member.setStatus(approved ? 1 : 2); // 1=已加入, 2=已退出(视作拒绝)
        member.setUpdateTime(new Date());
        clubMemberRepository.save(member);
        // 通知申请人结果
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", userId);
            payload.put("notificationType", 2); // 社团通知
            payload.put("title", approved ? "入团申请已通过" : "入团申请未通过");
            payload.put("content", approved ? "恭喜，您的入团申请已通过审核。" : "很抱歉，您的入团申请未获通过。");
            payload.put("relatedId", clubId);
            payload.put("relatedType", "club_join");
            restTemplate.postForEntity(gatewayBaseUrl + "/notifications", payload, Map.class);
        } catch (Exception ignored) {
        }
    }

    // 旧的按角色群发通知逻辑已移除，按需求仅通知社团负责人
}