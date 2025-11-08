package com.club.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 密码加密器（BCrypt算法，与你业务中密码加密逻辑匹配）
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 显式配置UserDetailsService，禁用Spring Security默认用户（解决启动报错）
    @Bean
    public UserDetailsService userDetailsService() {
        // 返回空的内存用户管理器，仅用于禁用默认用户，实际用户校验依赖你自定义的UserService
        return new InMemoryUserDetailsManager();
    }

    // 安全规则配置（适配你的登录接口路径，放行免认证接口）
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 放行登录接口的实际路径：/api/auth/login
                .antMatchers("/api/auth/login").permitAll()
                // 放行注销接口（若前端需要）
                .antMatchers("/api/auth/logout").permitAll()
                .anyRequest().authenticated();

        return http.build();
    }
}