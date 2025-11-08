package com.club.message.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

/**
 * 基于Redis的分布式锁工具类
 */
@Component
public class RedisLock {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 锁前缀（避免key冲突）
    private static final String LOCK_PREFIX = "message:lock:";
    // 锁默认过期时间（30秒，防止死锁）
    private static final long DEFAULT_EXPIRE = 30;

    /**
     * 尝试获取锁
     * @param key 业务key
     * @param expire 过期时间（秒）
     * @return 锁标识（null表示获取失败）
     */
    public String tryLock(String key, long expire) {
        String lockKey = LOCK_PREFIX + key;
        String lockValue = UUID.randomUUID().toString(); // 唯一标识，防止误删

        // SET NX + 过期时间：原子操作
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, expire, TimeUnit.SECONDS);

        return (success != null && success) ? lockValue : null;
    }

    /**
     * 尝试获取锁（使用默认过期时间）
     */
    public String tryLock(String key) {
        return tryLock(key, DEFAULT_EXPIRE);
    }

    /**
     * 释放锁
     * @param key 业务key
     * @param value 锁标识（获取锁时返回的value）
     * @return 是否释放成功
     */
    public boolean releaseLock(String key, String value) {
        if (value == null) {
            return false;
        }
        String lockKey = LOCK_PREFIX + key;
        Object currentValue = redisTemplate.opsForValue().get(lockKey);

        // 确认是自己的锁才释放（防止释放其他线程的锁）
        if (value.equals(currentValue)) {
            return redisTemplate.delete(lockKey);
        }
        return false;
    }
}