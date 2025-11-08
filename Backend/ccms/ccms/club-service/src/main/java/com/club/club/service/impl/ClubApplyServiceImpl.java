package com.club.club.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.club.club.dto.ClubApplyHandleDTO;
import com.club.common.dto.MessageDTO;
import com.club.club.entity.Club;
import com.club.club.entity.ClubApply;
import com.club.club.entity.ClubMember;
import com.club.club.mapper.ClubApplyMapper;
import com.club.club.mapper.ClubMapper;
import com.club.club.mapper.ClubMemberMapper;
import com.club.club.service.ClubApplyService;
import com.club.club.util.RedisLock;
import com.club.message.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class ClubApplyServiceImpl extends ServiceImpl<ClubApplyMapper, ClubApply> implements ClubApplyService {

    @Resource
    private ClubMapper clubMapper;

    @Resource
    private ClubMemberMapper clubMemberMapper;

    @Resource
    private RedisLock redisLock;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitApply(Long clubId, Long applicantId) {
        String lockKey = "club:apply:" + clubId + ":" + applicantId;
        String lockValue = redisLock.tryLock(lockKey, 3);
        if (lockValue == null) {
            throw new RuntimeException("操作频繁，请稍后再试");
        }

        try {
            Club club = clubMapper.selectById(clubId);
            if (club == null) {
                throw new RuntimeException("社团不存在");
            }
            if (club.getStatus() == 1) {
                throw new RuntimeException("社团已禁用，无法申请加入");
            }

            ClubMember existingMember = clubMemberMapper.selectByClubIdAndUserId(clubId, applicantId);
            if (existingMember != null) {
                throw new RuntimeException("您已加入该社团，无需重复申请");
            }

            ClubApply existingApply = baseMapper.selectByClubIdAndApplicantId(clubId, applicantId);
            if (existingApply != null) {
                throw new RuntimeException("您已提交过该社团的申请，请勿重复提交");
            }

            if (club.getFounderId().equals(applicantId)) {
                throw new RuntimeException("您是社团创始人，无需申请加入");
            }

            ClubApply apply = new ClubApply();
            apply.setClubId(clubId);
            apply.setApplicantId(applicantId);
            apply.setFounderId(club.getFounderId());
            apply.setStatus(0);
            apply.setApplyTime(LocalDateTime.now());

            MessageDTO message = new MessageDTO();
            message.setReceiverId(club.getFounderId());
            message.setContent("用户" + applicantId + "申请加入社团" + clubId + "，请及时处理");
            message.setType(1);
            rabbitTemplate.convertAndSend(RabbitConfig.CLUB_APPLY_QUEUE, message);

            return save(apply);
        } finally {
            redisLock.releaseLock(lockKey, lockValue);
        }
    }

    @Override
    public IPage<ClubApply> getPendingApplyList(Page<ClubApply> page, Long founderId) {
        return baseMapper.selectPendingByFounderId(page, founderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleApply(ClubApplyHandleDTO dto, Long operatorId) {
        String lockKey = "club:apply:handle:" + dto.getApplyId();
        String lockValue = redisLock.tryLock(lockKey, 3);
        if (lockValue == null) {
            throw new RuntimeException("操作频繁，请稍后再试");
        }

        try {
            ClubApply apply = getById(dto.getApplyId());
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
                ClubMember member = new ClubMember();
                member.setClubId(apply.getClubId());
                member.setUserId(apply.getApplicantId());
                member.setRole(0);
                member.setJoinTime(LocalDateTime.now());
                clubMemberMapper.insert(member);
            }

            MessageDTO resultMessage = new MessageDTO();
            resultMessage.setReceiverId(apply.getApplicantId());
            String content = dto.getStatus() == 1
                    ? "您已成功加入社团" + apply.getClubId()
                    : "您加入社团" + apply.getClubId() + "的申请已被拒绝";
            resultMessage.setContent(content);
            resultMessage.setType(2);
            rabbitTemplate.convertAndSend(RabbitConfig.APPLY_RESULT_QUEUE, resultMessage);

            return true;
        } finally {
            redisLock.releaseLock(lockKey, lockValue);
        }
    }
}