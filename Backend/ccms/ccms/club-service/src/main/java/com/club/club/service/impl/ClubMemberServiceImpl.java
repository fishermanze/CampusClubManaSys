package com.club.club.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.club.club.entity.Club;
import com.club.club.entity.ClubMember;
import com.club.club.mapper.ClubMapper;
import com.club.club.mapper.ClubMemberMapper;
import com.club.club.service.ClubMemberService;
import com.club.club.util.RedisLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClubMemberServiceImpl extends ServiceImpl<ClubMemberMapper, ClubMember> implements ClubMemberService {

    @Resource
    private ClubMapper clubMapper;

    @Resource
    private RedisLock redisLock; // 分布式锁，解决并发加入问题

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean joinClub(Long clubId, Long userId) {
        // 1. 加锁：防止同一用户并发加入同一社团
        String lockKey = "club:join:" + clubId + ":" + userId;
        String lockValue = redisLock.tryLock(lockKey, 3); // 锁3秒
        if (lockValue == null) {
            throw new RuntimeException("操作频繁，请稍后再试");
        }

        try {
            // 2. 校验社团是否存在且正常
            Club club = clubMapper.selectById(clubId);
            if (club == null) {
                throw new RuntimeException("社团不存在");
            }
            if (club.getStatus() == 1) {
                throw new RuntimeException("社团已被禁用，无法加入");
            }

            // 3. 校验是否已加入社团
            if (isMemberExists(clubId, userId)) {
                throw new RuntimeException("您已加入该社团，无需重复操作");
            }

            // 4. 校验是否为创始人（创始人无需重复加入）
            if (club.getFounderId().equals(userId)) {
                throw new RuntimeException("您是社团创始人，无需加入");
            }

            // 5. 保存成员记录（普通成员角色：0）
            ClubMember member = new ClubMember();
            member.setClubId(clubId);
            member.setUserId(userId);
            member.setRole(0); // 普通成员
            member.setJoinTime(LocalDateTime.now());

            return save(member);
        } finally {
            // 6. 释放锁
            redisLock.releaseLock(lockKey, lockValue);
        }
    }

    @Override
    public List<ClubMember> getMemberByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public boolean isMemberExists(Long clubId, Long userId) {
        ClubMember member = baseMapper.selectByClubIdAndUserId(clubId, userId);
        return member != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean kickMember(Long clubId, Long operatorId, Long kickedUserId) {
        // 1. 校验社团是否存在
        Club club = clubMapper.selectById(clubId);
        if (club == null) {
            throw new RuntimeException("社团不存在");
        }

        // 2. 校验操作者是否为社团成员且有权限（管理员/创始人）
        ClubMember operator = baseMapper.selectByClubIdAndUserId(clubId, operatorId);
        if (operator == null) {
            throw new RuntimeException("您不是该社团成员，无操作权限");
        }
        if (operator.getRole() < 1) { // 角色1=管理员，2=创始人
            throw new RuntimeException("权限不足，仅管理员/创始人可踢人");
        }

        // 3. 校验被踢用户是否为社团成员
        ClubMember kickedMember = baseMapper.selectByClubIdAndUserId(clubId, kickedUserId);
        if (kickedMember == null) {
            throw new RuntimeException("被踢用户不是该社团成员");
        }

        // 4. 禁止踢创始人
        if (kickedMember.getRole() == 2) {
            throw new RuntimeException("无法踢出社团创始人");
        }

        // 5. 禁止自踢
        if (operatorId.equals(kickedUserId)) {
            throw new RuntimeException("不能踢出自己");
        }

        // 6. 执行踢人（删除成员记录）
        return removeById(kickedMember.getId());
    }

    @Override
    public List<ClubMember> getMembersByClubId(Long clubId, Integer role) {
        // 校验社团是否存在
        if (clubMapper.selectById(clubId) == null) {
            throw new RuntimeException("社团不存在");
        }
        return baseMapper.selectByClubId(clubId, role);
    }

    @Override
    public Integer countMembersByClubId(Long clubId) {
        // 校验社团是否存在
        if (clubMapper.selectById(clubId) == null) {
            throw new RuntimeException("社团不存在");
        }
        return baseMapper.countByClubId(clubId);
    }
}