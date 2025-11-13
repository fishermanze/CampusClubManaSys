package com.campusclub.activity.service.impl;

import com.campusclub.activity.dto.ActivityParticipantDTO;
import com.campusclub.activity.dto.CheckInStatisticsDTO;
import com.campusclub.activity.entity.Activity;
import com.campusclub.activity.entity.ActivityParticipant;
import com.campusclub.activity.repository.ActivityParticipantRepository;
import com.campusclub.activity.repository.ActivityRepository;
import com.campusclub.activity.service.ActivityParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.time.Duration;
import java.util.Objects;

@Service
public class ActivityParticipantServiceImpl implements ActivityParticipantService {

    @Autowired
    private ActivityParticipantRepository participantRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String ENROLL_LOCK_PREFIX = "lock:activity:enroll:";
    private static final Duration DEFAULT_LOCK_WAIT = Duration.ofSeconds(3);
    private static final Duration DEFAULT_LOCK_LEASE = Duration.ofSeconds(10);

    @Override
    @Transactional
    public ActivityParticipant enrollActivity(ActivityParticipant participant) {
        // 使用getter方法获取属性值
        Long activityIdObj = Objects.requireNonNull(participant.getActivityId(), "活动ID不能为空");
        Long userIdObj = Objects.requireNonNull(participant.getUserId(), "用户ID不能为空");
        long activityId = activityIdObj.longValue();
        long userId = userIdObj.longValue();

        String lockKey = ENROLL_LOCK_PREFIX + activityId;
        String lockValue = UUID.randomUUID().toString();
        if (!acquireLock(lockKey, lockValue, DEFAULT_LOCK_WAIT, DEFAULT_LOCK_LEASE)) {
            throw new RuntimeException("系统繁忙，请稍后再试");
        }

        try {
            Activity activity = activityRepository.findById(activityIdObj)
                    .orElseThrow(() -> new RuntimeException("活动不存在"));

            if (activity.getMaxParticipants() != null && activity.getCurrentParticipants() != null
                    && activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
                throw new RuntimeException("活动名额已满");
            }

            if (participantRepository.existsByActivityIdAndUserId(activityIdObj, userIdObj)) {
                throw new RuntimeException("您已参与该活动");
            }

            participant.setEnrollmentTime(LocalDateTime.now());

            if (userId == 1L) {
                participant.setStatus(1); // 1-已通过
                participant.setApprovalTime(LocalDateTime.now());
                participant.setApprovedBy(1L);
            } else {
                participant.setStatus(0); // 0-待审批
                sendEnrollmentNotification(activity, userIdObj);
            }

            ActivityParticipant saved;
            try {
                saved = participantRepository.save(participant);
            } catch (OptimisticLockingFailureException e) {
                throw new RuntimeException("报名冲突，请稍后重试", e);
            }

            if (saved.getStatus() == 1) {
                // 使用乐观锁更新活动当前人数
                incrementParticipantsSafely(activity);
            }

            return saved;
        } finally {
            releaseLock(lockKey, lockValue);
        }
    }

    private void incrementParticipantsSafely(Activity activity) {
        boolean updated = false;
        int retry = 0;
        while (!updated && retry < 3) {
            try {
                Long activityId = Objects.requireNonNull(activity.getId(), "活动ID不存在");
                Activity managed = activityRepository.findById(activityId)
                        .orElseThrow(() -> new RuntimeException("活动不存在"));
                Integer current = Optional.ofNullable(managed.getCurrentParticipants()).orElse(0);
                managed.setCurrentParticipants(current + 1);
                activityRepository.save(managed);
                updated = true;
            } catch (OptimisticLockingFailureException ex) {
                retry++;
                if (retry >= 3) {
                    throw new RuntimeException("系统繁忙，稍后再试", ex);
                }
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("线程中断", ignored);
                }
            }
        }
    }

    private boolean acquireLock(String key, String value, Duration waitTimeout, Duration leaseTime) {
        key = Objects.requireNonNull(key, "lock key must not be null");
        value = Objects.requireNonNull(value, "lock value must not be null");
        waitTimeout = Objects.requireNonNull(waitTimeout, "waitTimeout must not be null");
        leaseTime = Objects.requireNonNull(leaseTime, "leaseTime must not be null");
        long end = System.currentTimeMillis() + waitTimeout.toMillis();
        while (System.currentTimeMillis() < end) {
            Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(
                    key, value, leaseTime);
            if (Boolean.TRUE.equals(success)) {
                return true;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    private void releaseLock(String key, String value) {
        key = Objects.requireNonNull(key, "lock key must not be null");
        value = Objects.requireNonNull(value, "lock value must not be null");
        try {
            String currentValue = stringRedisTemplate.opsForValue().get(key);
            if (value.equals(currentValue)) {
                stringRedisTemplate.delete(key);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * 发送活动报名审核通知
     */
    @SuppressWarnings("unchecked")
    private void sendEnrollmentNotification(Activity activity, Long userId) {
        userId = Objects.requireNonNull(userId, "用户ID不能为空");
        try {
            // 获取活动所属社团的社长（role=1）
            Long clubId = activity.getClubId();
            List<Long> notifyUserIds = new ArrayList<>();
            
            // 添加系统管理员（userId=1）
            notifyUserIds.add(1L);
            
            // 获取社团社长
            try {
                String clubUrl = "http://localhost:8083/clubs/" + clubId;
                Map<String, Object> clubInfo = restTemplate.getForObject(clubUrl, Map.class);
                if (clubInfo != null) {
                    // 获取社团成员列表，找到社长（role=1）
                    String membersUrl = "http://localhost:8083/clubs/" + clubId + "/members";
                    List<Map<String, Object>> members = restTemplate.getForObject(membersUrl, List.class);
                    if (members != null) {
                        for (Map<String, Object> member : members) {
                            Object role = member.get("role");
                            Object memberUserId = member.get("userId");
                            // role=1表示社长
                            if (role != null && (role.equals(1) || role.equals("1")) && memberUserId != null) {
                                Long presidentId = Long.parseLong(memberUserId.toString());
                                if (!notifyUserIds.contains(presidentId)) {
                                    notifyUserIds.add(presidentId);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("获取社团社长信息失败: " + e.getMessage());
            }
            
            // 获取用户信息
            String userName = "用户";
            try {
                String userUrl = "http://localhost:8082/users/" + userId;
                Map<String, Object> userInfo = restTemplate.getForObject(userUrl, Map.class);
                if (userInfo != null) {
                    userName = (String) userInfo.getOrDefault("realName", 
                            userInfo.getOrDefault("username", "用户"));
                }
            } catch (Exception e) {
                System.err.println("获取用户信息失败: " + e.getMessage());
            }
            
            // 发送通知给所有需要审核的用户
            for (Long notifyUserId : notifyUserIds) {
                try {
                    Map<String, Object> notification = new HashMap<>();
                    notification.put("userId", notifyUserId);
                    notification.put("notificationType", 3); // 3-活动通知
                    notification.put("title", "活动报名审核通知");
                    notification.put("content", String.format("用户 %s 报名参加了活动《%s》，请及时审核。", 
                            userName, activity.getTitle()));
                    notification.put("relatedId", activity.getId());
                    notification.put("relatedType", "activity_enrollment");
                    notification.put("needPush", true);
                    notification.put("status", 0); // 0-未读
                    
                    String notificationUrl = "http://localhost:8085/notifications";
                    restTemplate.postForObject(notificationUrl, notification, Map.class);
                } catch (Exception e) {
                    System.err.println("发送通知失败，用户ID: " + notifyUserId + ", 错误: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("发送活动报名审核通知失败: " + e.getMessage());
            // 通知发送失败不影响报名流程
        }
    }

    @Override
    @Transactional
    public void cancelEnrollment(Long activityId, Long userId) {
        Optional<ActivityParticipant> participantOpt = participantRepository.findByActivityIdAndUserId(activityId, userId);
        if (participantOpt.isPresent()) {
            ActivityParticipant participant = participantOpt.get();
            // 使用setter方法设置属性值
            participant.setStatus(4); // 4-已取消
            participantRepository.save(participant);

            // 减少活动当前参与人数
            activityRepository.decrementParticipantsCount(activityId);
        }
    }

    @Override
    @Transactional
    public void approveParticipation(Long activityId, Long userId, Integer status, String remark) {
        participantRepository.updateParticipationStatus(
                activityId,
                userId,
                status,
                LocalDateTime.now(),
                null, // 当前操作用户ID
                remark
        );
    }

    @Override
    @Transactional
    public void checkIn(Long activityId, Long userId) {
        participantRepository.checkIn(activityId, userId, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void checkOut(Long activityId, Long userId) {
        participantRepository.checkOut(activityId, userId, LocalDateTime.now());
    }

    @Override
    public ActivityParticipantDTO getParticipation(Long activityId, Long userId) {
        ActivityParticipant participant = participantRepository.findByActivityIdAndUserId(activityId, userId)
                .orElseThrow(() -> new RuntimeException("参与记录不存在"));

        ActivityParticipantDTO dto = mapToDTO(participant);
        setParticipationStatusText(dto);

        return dto;
    }

    @Override
    public Page<ActivityParticipantDTO> getActivityParticipants(Long activityId, Integer status, Pageable pageable) {
        pageable = Objects.requireNonNull(pageable, "pageable must not be null");
        Page<ActivityParticipant> participants;
        if (status != null) {
            participants = participantRepository.findAll(
                    (root, query, criteriaBuilder) -> criteriaBuilder.and(
                            criteriaBuilder.equal(root.get("activityId"), activityId),
                            criteriaBuilder.equal(root.get("status"), status)
                    ),
                    pageable
            );
        } else {
            participants = participantRepository.findAll(
                    (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("activityId"), activityId),
                    pageable
            );
        }

        return participants.map(participant -> {
            ActivityParticipantDTO dto = mapToDTO(participant);
            setParticipationStatusText(dto);
            return dto;
        });
    }

    @Override
    public Page<ActivityParticipantDTO> getUserParticipations(Long userId, Pageable pageable) {
        pageable = Objects.requireNonNull(pageable, "pageable must not be null");
        Page<ActivityParticipant> participants = participantRepository.findAll(
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId"), userId),
                pageable
        );

        return participants.map(participant -> {
            ActivityParticipantDTO dto = mapToDTO(participant);
            setParticipationStatusText(dto);
            return dto;
        });
    }

    @Override
    @Transactional
    public void batchApproveParticipations(Long activityId, List<Long> userIds, Integer status, String remark) {
        LocalDateTime now = LocalDateTime.now();
        for (Long userId : userIds) {
            participantRepository.updateParticipationStatus(
                    activityId, userId, status, now, null, remark
            );
        }
    }

    @Override
    @Transactional
    public void batchCheckIn(Long activityId, List<Long> userIds) {
        LocalDateTime now = LocalDateTime.now();
        for (Long userId : userIds) {
            participantRepository.checkIn(activityId, userId, now);
        }
    }

    @Override
    public byte[] exportParticipants(Long activityId, Integer status) {
        // 实现导出功能，返回Excel或CSV格式的字节数组
        return new byte[0];
    }

    @Override
    public long countParticipants(Long activityId, Integer status) {
        return participantRepository.countByActivityIdAndStatus(activityId, status);
    }

    @Override
    public boolean hasParticipated(Long activityId, Long userId) {
        return participantRepository.existsByActivityIdAndUserId(activityId, userId);
    }

    @Override
    public Integer getUserParticipationStatus(Long activityId, Long userId) {
        Optional<ActivityParticipant> participantOpt = participantRepository.findByActivityIdAndUserId(activityId, userId);
        // 使用getter方法获取属性值
        return participantOpt.map(p -> p.getStatus()).orElse(null);
    }

    @Override
    @Transactional
    public void updateEnrollmentInfo(Long activityId, Long userId, String enrollmentInfo) {
        ActivityParticipant participant = participantRepository.findByActivityIdAndUserId(activityId, userId)
                .orElseThrow(() -> new RuntimeException("参与记录不存在"));
        // 使用setter方法设置属性值
        participant.setEnrollmentInfo(enrollmentInfo);
        participantRepository.save(participant);
    }

    @Override
    public CheckInStatisticsDTO getCheckInStatistics(Long activityId) {
        CheckInStatisticsDTO statistics = new CheckInStatisticsDTO();

        // 统计应到人数（已批准的参与记录）
        long approvedCount = participantRepository.countByActivityIdAndStatus(activityId, 1);

        // 统计实到人数（已签到的参与记录）
        // 这里需要自定义查询来统计已签到的人数
        long checkedInCount = 0; // 简化实现

        // 使用setter方法设置属性值
        statistics.setExpectedParticipants(approvedCount);
        statistics.setActualParticipants(checkedInCount);
        statistics.setCheckInRate(approvedCount > 0 ? (double) checkedInCount / approvedCount : 0);
        statistics.setAbsentParticipants(approvedCount - checkedInCount);
        statistics.setCheckedInCount(checkedInCount);

        return statistics;
    }

    /**
     * 手动映射实体为DTO，避免ModelMapper歧义和懒加载问题
     */
    private ActivityParticipantDTO mapToDTO(ActivityParticipant participant) {
        ActivityParticipantDTO dto = new ActivityParticipantDTO();
        dto.setId(participant.getId());
        dto.setActivityId(participant.getActivityId());
        dto.setUserId(participant.getUserId());
        dto.setStatus(participant.getStatus());
        dto.setEnrollmentTime(participant.getEnrollmentTime());
        dto.setApprovalTime(participant.getApprovalTime());
        dto.setApprovedBy(participant.getApprovedBy());
        dto.setApprovalRemark(participant.getApprovalRemark());
        dto.setCheckInTime(participant.getCheckInTime());
        dto.setCheckOutTime(participant.getCheckOutTime());
        dto.setEnrollmentInfo(participant.getEnrollmentInfo());
        
        // 如果关联的活动已加载，设置活动标题
        if (participant.getActivity() != null) {
            dto.setActivityTitle(participant.getActivity().getTitle());
        }
        
        return dto;
    }

    private void setParticipationStatusText(ActivityParticipantDTO dto) {
        Integer status = dto.getStatus();
        String statusText;
        switch (status) {
            case 0: statusText = "待审批"; break;
            case 1: statusText = "已批准"; break;
            case 2: statusText = "已拒绝"; break;
            case 3: statusText = "已签到"; break;
            case 4: statusText = "已取消"; break;
            default: statusText = "未知"; break;
        }
        // 使用setter方法设置statusText属性
        dto.setStatusText(statusText);
    }
}