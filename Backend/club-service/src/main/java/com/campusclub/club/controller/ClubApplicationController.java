package com.campusclub.club.controller;

import com.campusclub.club.entity.ClubMember;
import com.campusclub.club.repository.ClubMemberRepository;
import com.campusclub.club.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clubs/applications")
public class ClubApplicationController {

    @Autowired
    private ClubMemberRepository clubMemberRepository;

    @Autowired
    private ClubRepository clubRepository;

    /**
     * 提交入团申请（包含申请信息）
     */
    @PostMapping("/{clubId}")
    public ClubMember applyToClub(@PathVariable Long clubId, @RequestBody ClubMember body) {
        ClubMember member = new ClubMember();
        member.setClubId(clubId);
        member.setUserId(body.getUserId());
        member.setRole(0);
        member.setStatus(0); // 待审核
        member.setJoinTime(new Date());
        member.setUpdateTime(new Date());
        member.setActivityCount(0);
        member.setTotalScore(0.0);
        member.setLevel(1);
        member.setYearEvaluationStatus(0);
        member.setRealName(body.getRealName());
        member.setGender(body.getGender());
        member.setMajor(body.getMajor());
        member.setClassName(body.getClassName());
        member.setReason(body.getReason());
        return clubMemberRepository.save(member);
    }

    /**
     * 获取某社团的入团申请列表（默认仅返回待审核）
     */
    @GetMapping("/club/{clubId}")
    public List<Map<String, Object>> getClubApplications(@PathVariable Long clubId,
                                                         @RequestParam(required = false, defaultValue = "0") Integer status) {
        List<ClubMember> list = status == null
                ? clubMemberRepository.findByClubId(clubId)
                : clubMemberRepository.findByClubIdAndStatus(clubId, status);
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * 获取某用户的入团申请列表
     */
    @GetMapping("/user/{userId}")
    public List<Map<String, Object>> getUserApplications(@PathVariable Long userId) {
        List<ClubMember> list = clubMemberRepository.findByUserId(userId);
        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * 获取单条申请详情
     */
    @GetMapping("/{memberId}")
    public Map<String, Object> getApplicationDetail(@PathVariable Long memberId) {
        ClubMember m = clubMemberRepository.findById(memberId).orElse(null);
        if (m == null) return null;
        return toDto(m);
    }

    /**
     * 取消入团申请（将状态置为3：已取消，仅允许待审核状态取消）
     */
    @PutMapping("/{memberId}/cancel")
    public void cancelApplication(@PathVariable Long memberId) {
        ClubMember m = clubMemberRepository.findById(memberId).orElse(null);
        if (m == null) {
            return;
        }
        // 仅待审核可取消
        if (m.getStatus() != null && m.getStatus() == 0) {
            m.setStatus(3);
            m.setUpdateTime(new Date());
            clubMemberRepository.save(m);
        }
    }

    private Map<String, Object> toDto(ClubMember m) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", m.getId());
        dto.put("clubId", m.getClubId());
        dto.put("userId", m.getUserId());
        dto.put("status", m.getStatus());
        dto.put("joinTime", m.getJoinTime());
        dto.put("realName", m.getRealName());
        dto.put("gender", m.getGender());
        dto.put("major", m.getMajor());
        dto.put("className", m.getClassName());
        dto.put("reason", m.getReason());
        dto.put("role", m.getRole());
        return dto;
    }
}

