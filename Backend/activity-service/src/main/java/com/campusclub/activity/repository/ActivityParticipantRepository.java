package com.campusclub.activity.repository;

import com.campusclub.activity.entity.ActivityParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityParticipantRepository extends JpaRepository<ActivityParticipant, Long>, JpaSpecificationExecutor<ActivityParticipant> {

    /**
     * 根据活动ID和用户ID查找参与记录
     */
    Optional<ActivityParticipant> findByActivityIdAndUserId(Long activityId, Long userId);

    /**
     * 查找活动的所有参与者
     */
    List<ActivityParticipant> findByActivityIdOrderByEnrollmentTimeDesc(Long activityId);

    /**
     * 根据活动ID和状态查找参与者
     */
    List<ActivityParticipant> findByActivityIdAndStatusOrderByEnrollmentTimeDesc(Long activityId, Integer status);

    /**
     * 查找用户参与的所有活动
     */
    List<ActivityParticipant> findByUserIdOrderByEnrollmentTimeDesc(Long userId);

    /**
     * 根据用户ID和状态查找参与记录
     */
    List<ActivityParticipant> findByUserIdAndStatusOrderByEnrollmentTimeDesc(Long userId, Integer status);

    /**
     * 更新参与状态
     */
    @Modifying
    @Query("update ActivityParticipant p set p.status = ?3, p.approvalTime = ?4, p.approvedBy = ?5, p.approvalRemark = ?6 where p.activityId = ?1 and p.userId = ?2")
    void updateParticipationStatus(Long activityId, Long userId, Integer status, LocalDateTime approvalTime, Long approvedBy, String approvalRemark);

    /**
     * 签到
     */
    @Modifying
    @Query("update ActivityParticipant p set p.checkInTime = ?3, p.status = 3 where p.activityId = ?1 and p.userId = ?2 and p.status = 1")
    void checkIn(Long activityId, Long userId, LocalDateTime checkInTime);

    /**
     * 退签
     */
    @Modifying
    @Query("update ActivityParticipant p set p.checkOutTime = ?3 where p.activityId = ?1 and p.userId = ?2 and p.checkInTime is not null")
    void checkOut(Long activityId, Long userId, LocalDateTime checkOutTime);

    /**
     * 统计活动参与人数
     */
    long countByActivityIdAndStatus(Long activityId, Integer status);

    /**
     * 统计用户参与的活动数量
     */
    long countByUserId(Long userId);

    /**
     * 检查用户是否已参与活动
     */
    boolean existsByActivityIdAndUserId(Long activityId, Long userId);
}