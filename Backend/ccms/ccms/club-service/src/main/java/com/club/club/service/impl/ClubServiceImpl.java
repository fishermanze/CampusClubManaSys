package com.club.club.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.club.club.dto.ClubCreateDTO;
import com.club.club.entity.Club;
import com.club.club.entity.ClubMember;
import com.club.club.mapper.ClubMapper;
import com.club.club.mapper.ClubMemberMapper;
import com.club.club.service.ClubService;
import com.club.club.util.RedisLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class ClubServiceImpl extends ServiceImpl<ClubMapper, Club> implements ClubService {

    @Resource
    private ClubMemberMapper clubMemberMapper; // 用于创建社团时自动添加创始人到成员表

    @Resource
    private RedisLock redisLock; // 分布式锁，解决并发创建问题

    @Override
    @Transactional(rollbackFor = Exception.class) // 事务保证：创建社团+添加创始人原子性
    public boolean createClub(ClubCreateDTO dto, Long founderId) {
        // 1. 对社团名称加分布式锁，防止并发创建重复名称
        String lockKey = "club:create:" + dto.getName();
        String lockValue = redisLock.tryLock(lockKey, 5); // 锁5秒，防止死锁
        if (lockValue == null) {
            throw new RuntimeException("操作频繁，请稍后再试");
        }

        try {
            // 2. 校验社团名称唯一性
            if (getClubByName(dto.getName()) != null) {
                throw new RuntimeException("社团名称已存在，请更换");
            }

            // 3. 校验用户是否已创建社团（限制1个）
            if (getClubByFounderId(founderId) != null) {
                throw new RuntimeException("一个用户只能创建一个社团");
            }

            // 4. 保存社团信息
            Club club = new Club();
            club.setName(dto.getName());
            club.setDescription(dto.getDescription());
            club.setFounderId(founderId);
            club.setCreateTime(LocalDateTime.now());
            club.setStatus(0); // 正常状态
            boolean saveSuccess = save(club);
            if (!saveSuccess) {
                throw new RuntimeException("社团创建失败");
            }

            // 5. 自动将创始人添加到社团成员表（角色为创始人：2）
            ClubMember founderMember = new ClubMember();
            founderMember.setClubId(club.getId());
            founderMember.setUserId(founderId);
            founderMember.setRole(2); // 创始人角色
            founderMember.setJoinTime(LocalDateTime.now());
            int insertCount = clubMemberMapper.insert(founderMember);
            if (insertCount <= 0) {
                throw new RuntimeException("创始人关联失败，社团创建回滚");
            }

            return true;
        } finally {
            // 6. 释放锁（无论成功失败都释放）
            redisLock.releaseLock(lockKey, lockValue);
        }
    }

    @Override
    public Club getClubByFounderId(Long founderId) {
        return baseMapper.selectByFounderId(founderId);
    }

    @Override
    public Club getClubByName(String name) {
        return baseMapper.selectByName(name);
    }
}