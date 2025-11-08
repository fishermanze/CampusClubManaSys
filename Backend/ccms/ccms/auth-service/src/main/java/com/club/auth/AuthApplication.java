package com.club.auth;

import org.mybatis.spring.annotation.MapperScan; // 导入MapperScan注解
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient // 开启服务注册发现
@MapperScan("com.club.auth.mapper") // 扫描Mapper接口所在包
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}