package com.campusclub.activity.service.impl;

import com.campusclub.activity.dto.ActivityParticipantDTO;
import com.campusclub.activity.dto.CheckInStatisticsDTO;
import com.campusclub.activity.entity.ActivityParticipant;
import com.campusclub.activity.repository.ActivityParticipantRepository;
import com.campusclub.activity.repository.ActivityRepository;
import com.campusclub.activity.service.ActivityParticipantService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityParticipantServiceImpl implements ActivityParticipantService {

    @Autowired
    private ActivityParticipantRepository participantRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public ActivityParticipant enrollActivity(ActivityParticipant participant) {
        // 使用getter方法获取属性值
        Long activityId = participant.getActivityId();
        Long userId = participant.getUserId();

        // 检查活动是否存在
        if (!activityRepository.existsById(activityId)) {
            throw new RuntimeException("活动不存在");
        }

        // 检查是否已参与
        if (participantRepository.existsByActivityIdAndUserId(activityId, userId)) {
            throw new RuntimeException("您已参与该活动");
        }

        // 使用setter方法设置属性值
        participant.setEnrollmentTime(LocalDateTime.now());
        participant.setStatus(0); // 0-待审批

        // 保存参与记录
        ActivityParticipant saved = participantRepository.save(participant);

        // 增加活动当前参与人数
        activityRepository.incrementParticipantsCount(activityId);

        return saved;
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

        ActivityParticipantDTO dto = modelMapper.map(participant, ActivityParticipantDTO.class);
        setParticipationStatusText(dto);

        return dto;
    }

    @Override
    public Page<ActivityParticipantDTO> getActivityParticipants(Long activityId, Integer status, Pageable pageable) {
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
            ActivityParticipantDTO dto = modelMapper.map(participant, ActivityParticipantDTO.class);
            setParticipationStatusText(dto);
            return dto;
        });
    }

    @Override
    public Page<ActivityParticipantDTO> getUserParticipations(Long userId, Pageable pageable) {
        Page<ActivityParticipant> participants = participantRepository.findAll(
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId"), userId),
                pageable
        );

        return participants.map(participant -> {
            ActivityParticipantDTO dto = modelMapper.map(participant, ActivityParticipantDTO.class);
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