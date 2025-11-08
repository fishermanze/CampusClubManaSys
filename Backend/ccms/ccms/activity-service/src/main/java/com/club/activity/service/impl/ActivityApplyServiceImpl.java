package com.club.activity.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.club.activity.dto.ActivityApplyDTO;
import com.club.activity.dto.ActivityHandleDTO;
import com.club.activity.entity.Activity;
import com.club.activity.entity.ActivityApply;
import com.club.activity.entity.ActivityParticipant;
import com.club.activity.mapper.ActivityApplyMapper;
import com.club.activity.mapper.ActivityMapper;
import com.club.activity.mapper.ActivityParticipantMapper;
import com.club.activity.service.ActivityApplyService;
import com.club.activity.util.RedisLock;
import com.club.common.dto.MessageDTO;
import com.club.activity.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class ActivityApplyServiceImpl extends ServiceImpl<ActivityApplyMapper, ActivityApply> implements ActivityApplyService {

    @Resource
    private ActivityMapper activityMapper;

    @Resource
    private ActivityParticipantMapper participantMapper;

    @Resource
    private RedisLock redisLock;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitApply(ActivityApplyDTO dto, Long applicantId) {
        String lockKey = "activity:apply:" + dto.getActivityId() + ":" + applicantId;
        String lockValue = redisLock.tryLock(lockKey, 3);
        if (lockValue == null) {
            throw new RuntimeException("操作频繁，请稍后再试");
        }

        try {
            Activity activity = activityMapper.selectById(dto.getActivityId());
            if (activity == null) {
                throw new RuntimeException("活动不存在");
            }
            if (activity.getStatus() != 1) {
                throw new RuntimeException("活动已结束或未发布，无法申请");
            }

            ActivityApply existingApply = baseMapper.selectByActivityIdAndApplicantId(dto.getActivityId(), applicantId);
            if (existingApply != null) {
                throw new RuntimeException("您已提交过该活动的申请，请勿重复提交");
            }

            ActivityApply apply = new ActivityApply();
            apply.setActivityId(dto.getActivityId());
            apply.setApplicantId(applicantId);
            apply.setFounderId(activity.getFounderId());
            apply.setStatus(0);
            apply.setApplyTime(LocalDateTime.now());

            MessageDTO message = new MessageDTO();
            message.setReceiverId(activity.getFounderId());
            message.setContent("用户" + applicantId + "申请参与活动" + activity.getTitle() + "，请及时处理");
            message.setType(3); // 活动申请通知类型
            rabbitTemplate.convertAndSend(RabbitConfig.ACTIVITY_APPLY_QUEUE, message);

            return save(apply);
        } finally {
            redisLock.releaseLock(lockKey, lockValue);
        }
    }

    @Override
    public IPage<ActivityApply> getPendingApplyList(Page<ActivityApply> page, Long founderId) {
        return baseMapper.selectPendingByFounderId(page, founderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleApply(ActivityHandleDTO dto, Long operatorId) {
        String lockKey = "activity:apply:handle:" + dto.getApplyId();
        String lockValue = redisLock.tryLock(lockKey, 3);
        if (lockValue == null) {
            throw new RuntimeException("操作频繁，请稍后再试");
        }

        try {
            ActivityApply apply = getById(dto.getApplyId());
            if (apply == null) {
                throw new RuntimeException("申请记录不存在");
            }
            if (apply.getStatus() != 0) {
                throw new RuntimeException("该申请已处理，无需重复操作");
            }
            if (!apply.getFounderId().equals(operatorId)) {
                throw new RuntimeException("无权限处理该申请");
            }

            apply.setStatus(dto.getStatus());
            apply.setHandleTime(LocalDateTime.now());
            boolean updateSuccess = updateById(apply);
            if (!updateSuccess) {
                throw new RuntimeException("处理申请失败");
            }

            if (dto.getStatus() == 1) {
                ActivityParticipant participant = new ActivityParticipant();
                participant.setActivityId(apply.getActivityId());
                participant.setUserId(apply.getApplicantId());
                participant.setJoinTime(LocalDateTime.now());
                participantMapper.insert(participant);
            }

            Activity activity = activityMapper.selectById(apply.getActivityId());
            MessageDTO resultMessage = new MessageDTO();
            resultMessage.setReceiverId(apply.getApplicantId());
            String content = dto.getStatus() == 1
                    ? "您已成功参与活动" + activity.getTitle()
                    : "您参与活动" + activity.getTitle() + "的申请已被拒绝";
            resultMessage.setContent(content);
            resultMessage.setType(4); // 活动申请结果类型
            rabbitTemplate.convertAndSend(RabbitConfig.ACTIVITY_RESULT_QUEUE, resultMessage);

            return true;
        } finally {
            redisLock.releaseLock(lockKey, lockValue);
        }
    }
}