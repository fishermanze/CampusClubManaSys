package com.campusclub.stats.service;

import java.util.List;
import java.util.Map;
import java.util.Date;

public interface StatsService {
    // 获取所有社团活动数和人数比值的折线统计图数据
    List<Map<String, Object>> getClubActivityRatioStats(Date startDate, Date endDate);
    
    // 获取单个社团内成员参与活动数的排名表
    List<Map<String, Object>> getMemberActivityRanking(Long clubId, Integer limit);
    
    // 获取每月活动平均评分的折线图
    List<Map<String, Object>> getMonthlyActivityScoreStats(Date startDate, Date endDate, Long clubId);
    
    // 生成统计数据
    void generateClubActivityRatioStats();
    void generateMemberActivityRanking(Long clubId);
    void generateMonthlyActivityScoreStats();
    
    // 获取所有社团的数据统计
    Map<String, Object> getAllClubsStats();
    
    // 获取单个社团的数据统计
    Map<String, Object> getClubStats(Long clubId);
    
    // 获取活动平均评分
    Map<String, Object> getActivityAverageScore(Long activityId);
}