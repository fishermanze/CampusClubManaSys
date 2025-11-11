package com.campusclub.gateway.filter;

import com.campusclub.gateway.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private JwtUtils jwtUtils;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 跳过OPTIONS预检请求（CORS预检）
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequest().getMethod().name())) {
                return chain.filter(exchange);
            }

            // 从请求头中获取token
            String token = extractToken(exchange);

            // 如果没有token，返回401错误
            if (token == null) {
                return handleUnauthorized(exchange);
            }

            // 验证token
            if (!jwtUtils.validateToken(token)) {
                return handleUnauthorized(exchange);
            }

            try {
                // 从token中获取用户信息
                String username = jwtUtils.getUsernameFromToken(token);
                Claims claims = jwtUtils.parseToken(token);
                String role = claims.get("role", String.class);
                Object userIdObj = claims.get("userId");
                String userId = userIdObj != null ? userIdObj.toString() : null;

                // 将用户信息添加到请求头中，传递给下游服务
                ServerWebExchange mutatedExchange = exchange.mutate()
                        .request(exchange.getRequest().mutate()
                                .header("X-User-Name", username)
                                .header("X-User-Role", role != null ? role : "")
                                .header("X-User-Id", userId != null ? userId : "")
                                .build())
                        .build();

                return chain.filter(mutatedExchange);
            } catch (Exception e) {
                return handleUnauthorized(exchange);
            }
        };
    }

    /**
     * 从请求头中提取token
     */
    private String extractToken(ServerWebExchange exchange) {
        String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 处理未授权的请求
     */
    private Mono<Void> handleUnauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        // 可以添加配置参数
    }
}