package com.example.ccms.util;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class LockUtil {
    private final RedissonClient redissonClient;

    /**
     * 获取分布式锁
     * @param lockKey 锁键
     * @param expireTime 过期时间（秒）
     * @return 锁对象
     */
    public RLock lock(String lockKey, long expireTime) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(expireTime, TimeUnit.SECONDS);
        return lock;
    }

    /**
     * 尝试获取锁（非阻塞）
     * @param lockKey 锁键
     * @param waitTime 等待时间（秒）
     * @param expireTime 过期时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, long waitTime, long expireTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, expireTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * 释放锁
     * @param lockKey 锁键
     */
    public void releaseLock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 释放锁（通过锁对象）
     * @param lock 锁对象
     */
    public void releaseLock(RLock lock) {
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}