// api-gateway/src/main/java/com/club/gateway/filter/AuthFilter.java
package com.club.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import java.security.Key;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    @Value("${jwt.secret}")
    private String secret;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public AuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String token = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            token = token.substring(7);

            try {
                // 1. 生成符合 HS256 算法的 SecretKey
                SecretKey signingKey = Keys.hmacShaKeyFor(secret.getBytes());

                // 2. 验证 JWT 签名和有效性（显式转换为 SecretKey）
                Claims claims = Jwts.parser()
                        .verifyWith(signingKey) // 此处需传入 SecretKey 类型
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                // 3. 解析用户 ID
                Long userId = Long.parseLong(claims.getSubject());

                // 4. 验证 Redis 中是否存在有效 Token
                String redisToken = (String) redisTemplate.opsForValue().get("jwt:user:" + userId);
                if (redisToken == null || !redisToken.equals(token)) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                return chain.filter(exchange);
            } catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    public static class Config {}
}