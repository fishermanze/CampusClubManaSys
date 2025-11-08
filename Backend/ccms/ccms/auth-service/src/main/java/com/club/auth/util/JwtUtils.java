package com.club.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret; // 必须≥32位

    @Value("${jwt.expire}")
    private long expire; // 毫秒

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 生成符合 HS256 算法的 SecretKey
     */
    private SecretKey getSigningKey() {
        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT密钥长度必须至少32位");
        }
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成 JWT 令牌
     */
    public String generateToken(Long userId) {
        try {
            Date now = new Date();
            Date expireDate = new Date(now.getTime() + expire);

            String token = Jwts.builder()
                    .subject(userId.toString())
                    .issuedAt(now)
                    .expiration(expireDate)
                    .signWith(getSigningKey())
                    .compact();

            log.info("生成Token成功，用户ID：{}，过期时间：{}", userId, expireDate);
            return token;
        } catch (Exception e) {
            log.error("生成Token失败，用户ID：{}，异常信息：{}", userId, e.getMessage());
            throw new RuntimeException("Token生成失败", e);
        }
    }

    /**
     * 验证令牌有效性（包含Redis校验）
     */
    public boolean validateToken(String token) {
        try {
            // 解析JWT
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            // 校验Redis中的Token
            Long userId = getUserIdFromToken(token);
            String redisToken = (String) redisTemplate.opsForValue().get("jwt:user:" + userId);
            boolean isMatch = token.equals(redisToken);

            log.info("Token验证{}，用户ID：{}", isMatch ? "通过" : "失败", userId);
            return isMatch;
        } catch (Exception e) {
            log.warn("Token验证失败，异常信息：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 从令牌中解析用户 ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Long userId = Long.parseLong(claims.getSubject());
            log.info("解析Token成功，用户ID：{}", userId);
            return userId;
        } catch (Exception e) {
            log.error("解析Token失败，异常信息：{}", e.getMessage());
            throw new RuntimeException("Token解析失败", e);
        }
    }

    /**
     * 保存令牌到 Redis
     */
    public void saveTokenToRedis(Long userId, String token) {
        try {
            redisTemplate.opsForValue().set(
                    "jwt:user:" + userId,
                    token,
                    expire,
                    TimeUnit.MILLISECONDS
            );
            log.info("Token保存到Redis成功，用户ID：{}", userId);
        } catch (Exception e) {
            log.error("Token保存到Redis失败，用户ID：{}，异常信息：{}", userId, e.getMessage());
            throw new RuntimeException("Token保存到Redis失败", e);
        }
    }

    /**
     * 从 Redis 删除令牌（注销用）
     */
    public void removeTokenFromRedis(Long userId) {
        try {
            redisTemplate.delete("jwt:user:" + userId);
            log.info("从Redis删除Token成功，用户ID：{}", userId);
        } catch (Exception e) {
            log.error("从Redis删除Token失败，用户ID：{}，异常信息：{}", userId, e.getMessage());
            throw new RuntimeException("从Redis删除Token失败", e);
        }
    }
}