package com.example.ccms.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置：
 * 1. 配置Redisson客户端连接Redis
 * 2. 提供分布式锁、分布式集合等高级功能支持
 */
@Configuration
public class RedissonConfig {

    /**
     * Redisson客户端实例：
     * 连接本地Redis（默认端口6379），可根据环境修改地址
     */
    @Bean
    public RedissonClient redissonClient() {
        // 创建配置对象
        Config config = new Config();
        // 单节点模式（生产环境可改为集群模式）
        config.useSingleServer()
                .setAddress("redis://localhost:6379") // Redis服务地址
                .setDatabase(0) // 数据库索引（默认0）
                .setConnectionPoolSize(10) // 连接池大小
                .setConnectTimeout(3000); // 连接超时时间（毫秒）

        // 创建并返回Redisson客户端
        return Redisson.create(config);
    }
}