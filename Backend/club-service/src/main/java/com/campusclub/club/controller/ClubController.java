package com.campusclub.club.controller;

import com.campusclub.club.entity.Club;
import com.campusclub.club.entity.ClubMember;
import com.campusclub.club.service.ClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clubs")
public class ClubController {

    @Autowired
    private ClubService clubService;

    // 社团管理接口
    @PostMapping
    public Club createClub(@RequestBody Club club) {
        return clubService.createClub(club);
    }

    @PutMapping("/{id}")
    public Club updateClub(@PathVariable Long id, @RequestBody Club club) {
        club.setId(id);
        return clubService.updateClub(club);
    }

    @GetMapping("/{id}")
    public Club getClubById(@PathVariable Long id) {
        return clubService.getClubById(id);
    }

    @GetMapping
    public List<Club> getAllClubs() {
        return clubService.getAllClubs();
    }

    @GetMapping("/search")
    public List<Club> searchClubs(@RequestParam String keyword) {
        return clubService.searchClubs(keyword);
    }

    @PutMapping("/{id}/status")
    public void updateClubStatus(@PathVariable Long id, @RequestParam Integer status) {
        clubService.updateClubStatus(id, status);
    }

    @GetMapping("/category/{category}")
    public List<Club> getClubsByCategory(@PathVariable String category) {
        return clubService.getClubsByCategory(category);
    }

    @GetMapping("/leader/{leaderId}")
    public List<Club> getClubsByLeader(@PathVariable Long leaderId) {
        return clubService.getClubsByLeaderId(leaderId);
    }

    // 社团成员管理接口
    @PostMapping("/{clubId}/members")
    public ClubMember joinClub(@PathVariable Long clubId, @RequestParam Long userId) {
        return clubService.joinClub(clubId, userId);
    }

    // 审批成员加入申请（通过/拒绝）
    @PutMapping("/{clubId}/members/{userId}/approve")
    public void approveMember(@PathVariable Long clubId,
                              @PathVariable Long userId,
                              @RequestParam boolean approved,
                              @RequestParam(required = false) Long approverId) {
        clubService.approveMember(clubId, userId, approved, approverId);
    }

    @DeleteMapping("/{clubId}/members/{userId}")
    public void leaveClub(@PathVariable Long clubId, @PathVariable Long userId) {
        clubService.leaveClub(clubId, userId);
    }

    @PutMapping("/{clubId}/members/{userId}/status")
    public void updateMemberStatus(@PathVariable Long clubId, @PathVariable Long userId, 
                                  @RequestParam Integer status) {
        clubService.updateMemberStatus(clubId, userId, status);
    }

    @PutMapping("/{clubId}/members/{userId}/level")
    public void updateMemberLevel(@PathVariable Long clubId, @PathVariable Long userId, 
                                 @RequestParam Integer level) {
        clubService.updateMemberLevel(clubId, userId, level);
    }

    @GetMapping("/{clubId}/members")
    public List<ClubMember> getClubMembers(@PathVariable Long clubId) {
        return clubService.getClubMembers(clubId);
    }

    @GetMapping("/user/{userId}")
    public List<ClubMember> getUserClubs(@PathVariable Long userId) {
        return clubService.getUserClubs(userId);
    }

    @PutMapping("/{clubId}/members/{userId}/expel")
    public void expelMember(@PathVariable Long clubId, @PathVariable Long userId) {
        clubService.expelMember(clubId, userId);
    }

    @PutMapping("/{clubId}/members/{userId}/evaluate")
    public void evaluateMember(@PathVariable Long clubId, @PathVariable Long userId, 
                              @RequestParam Double score) {
        clubService.evaluateMember(clubId, userId, score);
    }

    @GetMapping("/{clubId}/members/{userId}/year-evaluation")
    public Map<String, Object> getMemberYearEvaluation(@PathVariable Long clubId, 
                                                     @PathVariable Long userId) {
        return clubService.getMemberYearEvaluation(clubId, userId);
    }

    // 统计相关接口
    @GetMapping("/{clubId}/member-count")
    public Integer getMemberCount(@PathVariable Long clubId) {
        return clubService.getMemberCount(clubId);
    }

    @GetMapping("/{clubId}/top-members")
    public List<ClubMember> getTopActiveMembers(@PathVariable Long clubId, 
                                              @RequestParam(defaultValue = "10") Integer limit) {
        return clubService.getTopActiveMembers(clubId, limit);
    }
}