package com.club.club.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

@Component
public class RedisLock {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 锁的前缀
    private static final String LOCK_PREFIX = "club:lock:";
    // 锁的默认过期时间（30秒）
    private static final long LOCK_EXPIRE = 30;

    // 获取锁
    public String tryLock(String key, long timeout) {
        String lockKey = LOCK_PREFIX + key;
        String value = UUID.randomUUID().toString(); // 唯一标识，防止误删

        // 尝试获取锁（SET NX + 过期时间）
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, value, timeout, TimeUnit.SECONDS);

        return success != null && success ? value : null;
    }

    // 释放锁
    public boolean releaseLock(String key, String value) {
        String lockKey = LOCK_PREFIX + key;
        Object currentValue = redisTemplate.opsForValue().get(lockKey);

        // 确认是自己的锁才释放
        if (currentValue != null && currentValue.equals(value)) {
            return redisTemplate.delete(lockKey);
        }
        return false;
    }
}