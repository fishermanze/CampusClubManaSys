package com.campusclub.stats.repository;

import com.campusclub.stats.entity.StatData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Date;

@Repository
public interface StatDataRepository extends JpaRepository<StatData, Long> {
    List<StatData> findByStatType(String statType);
    List<StatData> findByClubIdAndStatType(Long clubId, String statType);
    List<StatData> findByStatTypeAndStatDateBetween(String statType, Date startDate, Date endDate);
    List<StatData> findByClubIdAndStatTypeAndStatDateBetween(Long clubId, String statType, Date startDate, Date endDate);
}