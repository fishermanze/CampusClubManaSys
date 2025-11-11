package com.campusclub.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*") // 在生产环境中应该配置具体的域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*", "Authorization", "X-Requested-With", "Content-Type")
                .exposedHeaders("*", "X-User-Id", "X-User-Role")
                .allowCredentials(true)
                .maxAge(3600); // 预检请求的缓存时间
    }
}