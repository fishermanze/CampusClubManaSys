package com.campusclub.stats.controller;

import com.campusclub.stats.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Date;

@RestController
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    // 生成统计数据的接口
    @PostMapping("/generate/club-activity-ratio")
    public void generateClubActivityRatioStats() {
        statsService.generateClubActivityRatioStats();
    }

    @PostMapping("/generate/member-ranking/{clubId}")
    public void generateMemberActivityRanking(@PathVariable Long clubId) {
        statsService.generateMemberActivityRanking(clubId);
    }

    @PostMapping("/generate/monthly-scores")
    public void generateMonthlyActivityScoreStats() {
        statsService.generateMonthlyActivityScoreStats();
    }

    // 数据查询接口
    @GetMapping("/club-activity-ratio")
    public List<Map<String, Object>> getClubActivityRatioStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return statsService.getClubActivityRatioStats(startDate, endDate);
    }

    @GetMapping("/club/{clubId}/member-ranking")
    public List<Map<String, Object>> getMemberActivityRanking(
            @PathVariable Long clubId,
            @RequestParam(defaultValue = "10") Integer limit) {
        return statsService.getMemberActivityRanking(clubId, limit);
    }

    @GetMapping("/monthly-scores")
    public List<Map<String, Object>> getMonthlyActivityScoreStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(required = false) Long clubId) {
        return statsService.getMonthlyActivityScoreStats(startDate, endDate, clubId);
    }

    // 综合统计接口
    @GetMapping("/all-clubs")
    public Map<String, Object> getAllClubsStats() {
        return statsService.getAllClubsStats();
    }

    @GetMapping("/club/{clubId}")
    public Map<String, Object> getClubStats(@PathVariable Long clubId) {
        return statsService.getClubStats(clubId);
    }

    @GetMapping("/activity/{activityId}/average-score")
    public Map<String, Object> getActivityAverageScore(@PathVariable Long activityId) {
        return statsService.getActivityAverageScore(activityId);
    }
}