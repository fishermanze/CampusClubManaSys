package com.club.activity.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RedisLock {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String LOCK_PREFIX = "activity:lock:";
    private static final long DEFAULT_EXPIRE = 30;

    public String tryLock(String key, long expire) {
        String lockKey = LOCK_PREFIX + key;
        String lockValue = UUID.randomUUID().toString();
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, expire, TimeUnit.SECONDS);
        return (success != null && success) ? lockValue : null;
    }

    public String tryLock(String key) {
        return tryLock(key, DEFAULT_EXPIRE);
    }

    public boolean releaseLock(String key, String value) {
        if (value == null) return false;
        String lockKey = LOCK_PREFIX + key;
        Object currentValue = redisTemplate.opsForValue().get(lockKey);
        if (value.equals(currentValue)) {
            return redisTemplate.delete(lockKey);
        }
        return false;
    }
}