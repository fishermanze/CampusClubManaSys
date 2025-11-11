package com.campusclub.activity.repository;

import com.campusclub.activity.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long>, JpaSpecificationExecutor<Activity> {

    /**
     * 根据社团ID查找活动
     */
    List<Activity> findByClubIdOrderByCreatedAtDesc(Long clubId);

    /**
     * 根据社团ID和状态查找活动
     */
    List<Activity> findByClubIdAndStatusOrderByCreatedAtDesc(Long clubId, Integer status);

    /**
     * 查找所有已发布的活动（按开始时间倒序）
     */
    List<Activity> findByStatusOrderByStartTimeDesc(Integer status);

    /**
     * 查找即将开始的活动
     */
    @Query("select a from Activity a where a.status = 1 and a.startTime > ?1 order by a.startTime asc")
    List<Activity> findUpcomingActivities(LocalDateTime now);

    /**
     * 查找正在进行的活动
     */
    @Query("select a from Activity a where a.status = 2 or (a.status = 1 and a.startTime <= ?1 and a.endTime >= ?1)")
    List<Activity> findCurrentActivities(LocalDateTime now);

    /**
     * 根据标题或描述搜索活动
     */
    @Query("select a from Activity a where (a.title like %?1% or a.description like %?1%) and a.status in (1, 2, 3)")
    List<Activity> searchActivities(String keyword);

    /**
     * 更新活动状态
     */
    @Modifying
    @Query("update Activity a set a.status = ?2 where a.id = ?1")
    void updateActivityStatus(Long id, Integer status);

    /**
     * 增加参与人数
     */
    @Modifying
    @Query("update Activity a set a.currentParticipants = a.currentParticipants + 1 where a.id = ?1")
    void incrementParticipantsCount(Long id);

    /**
     * 减少参与人数
     */
    @Modifying
    @Query("update Activity a set a.currentParticipants = a.currentParticipants - 1 where a.id = ?1 and a.currentParticipants > 0")
    void decrementParticipantsCount(Long id);

    /**
     * 根据标签查找活动
     */
    @Query("select a from Activity a where a.tags like %?1% and a.status in (1, 2, 3)")
    List<Activity> findByTag(String tag);
}