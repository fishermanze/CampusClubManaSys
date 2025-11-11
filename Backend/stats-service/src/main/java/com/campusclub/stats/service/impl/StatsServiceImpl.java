package com.campusclub.stats.service.impl;

import com.campusclub.stats.entity.StatData;
import com.campusclub.stats.repository.StatDataRepository;
import com.campusclub.stats.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private StatDataRepository statDataRepository;

    @Override
    public List<Map<String, Object>> getClubActivityRatioStats(Date startDate, Date endDate) {
        List<StatData> stats = statDataRepository.findByStatTypeAndStatDateBetween("club_activity_ratio", startDate, endDate);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (StatData stat : stats) {
            Map<String, Object> data = new HashMap<>();
            data.put("date", stat.getStatDate());
            data.put("clubName", stat.getDataKey());
            data.put("activityRatio", stat.getDataValue());
            result.add(data);
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getMemberActivityRanking(Long clubId, Integer limit) {
        List<StatData> stats = statDataRepository.findByClubIdAndStatType(clubId, "member_activity_rank");
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 排序并限制结果数量
        stats.sort((a, b) -> b.getDataValue().compareTo(a.getDataValue()));
        List<StatData> topStats = stats.subList(0, Math.min(limit, stats.size()));
        
        for (int i = 0; i < topStats.size(); i++) {
            StatData stat = topStats.get(i);
            Map<String, Object> data = new HashMap<>();
            data.put("rank", i + 1);
            data.put("memberName", stat.getDataKey());
            data.put("activityCount", stat.getDataValue());
            result.add(data);
        }
        
        return result;
    }

    @Override
    public List<Map<String, Object>> getMonthlyActivityScoreStats(Date startDate, Date endDate, Long clubId) {
        List<StatData> stats;
        if (clubId != null) {
            stats = statDataRepository.findByClubIdAndStatTypeAndStatDateBetween(clubId, "monthly_score_avg", startDate, endDate);
        } else {
            stats = statDataRepository.findByStatTypeAndStatDateBetween("monthly_score_avg", startDate, endDate);
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (StatData stat : stats) {
            Map<String, Object> data = new HashMap<>();
            data.put("month", stat.getDataKey());
            data.put("averageScore", stat.getDataValue());
            if (clubId != null) {
                data.put("clubId", clubId);
            }
            result.add(data);
        }
        
        return result;
    }

    @Override
    @Transactional
    public void generateClubActivityRatioStats() {
        // 这里应该调用club-service和activity-service获取数据进行计算
        // 暂时生成模拟数据
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        for (int i = 0; i < 6; i++) {
            LocalDate date = now.minusMonths(i);
            String month = date.format(formatter);
            
            // 模拟三个社团的数据
            for (int j = 1; j <= 3; j++) {
                StatData stat = new StatData();
                stat.setStatType("club_activity_ratio");
                stat.setClubId((long) j);
                stat.setDataKey("社团" + j);
                stat.setDataValue(Math.random() * 5 + 0.5); // 0.5-5.5之间的随机值
                stat.setStatDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                statDataRepository.save(stat);
            }
        }
    }

    @Override
    @Transactional
    public void generateMemberActivityRanking(Long clubId) {
        // 模拟生成社团成员活动排名数据
        for (int i = 1; i <= 10; i++) {
            StatData stat = new StatData();
            stat.setStatType("member_activity_rank");
            stat.setClubId(clubId);
            stat.setDataKey("成员" + i);
            stat.setDataValue((double) (20 - i)); // 20-11的递减值
            stat.setStatDate(new Date());
            statDataRepository.save(stat);
        }
    }

    @Override
    @Transactional
    public void generateMonthlyActivityScoreStats() {
        // 模拟生成每月活动平均评分数据
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        for (int i = 0; i < 12; i++) {
            LocalDate date = now.minusMonths(i);
            String month = date.format(formatter);
            
            StatData stat = new StatData();
            stat.setStatType("monthly_score_avg");
            stat.setDataKey(month);
            stat.setDataValue(Math.random() * 2 + 3); // 3.0-5.0之间的随机值
            stat.setStatDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            statDataRepository.save(stat);
        }
    }

    @Override
    public Map<String, Object> getAllClubsStats() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalClubs", 20); // 模拟数据
        result.put("totalMembers", 500);
        result.put("totalActivities", 120);
        result.put("averageActivityScore", 4.2);
        return result;
    }

    @Override
    public Map<String, Object> getClubStats(Long clubId) {
        Map<String, Object> result = new HashMap<>();
        result.put("clubId", clubId);
        result.put("memberCount", 30); // 模拟数据
        result.put("activityCount", 15);
        result.put("averageActivityScore", 4.5);
        result.put("topMembers", getMemberActivityRanking(clubId, 5));
        return result;
    }

    @Override
    public Map<String, Object> getActivityAverageScore(Long activityId) {
        Map<String, Object> result = new HashMap<>();
        result.put("activityId", activityId);
        result.put("averageScore", Math.random() * 2 + 3); // 3.0-5.0之间的随机值
        result.put("evaluationCount", 25); // 模拟评分人数
        return result;
    }
}