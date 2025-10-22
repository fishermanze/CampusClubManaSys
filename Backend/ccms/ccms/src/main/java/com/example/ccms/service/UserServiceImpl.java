package com.example.ccms.service;

import com.example.ccms.dto.request.LoginRequest;
import com.example.ccms.dto.request.RegisterRequest;
import com.example.ccms.dto.response.LoginResponse;
import com.example.ccms.entity.Users;
import com.example.ccms.exception.BusinessException;
import com.example.ccms.repository.UsersRepository;
import com.example.ccms.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    // 注册
    @Override
    public void register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (usersRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        // 创建用户并加密密码
        Users user = new Users();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUid(request.getUid()); // 校园统一标识
        user.setRealName(request.getRealName());
        usersRepository.save(user);
    }

    // 登录
    @Override
    public LoginResponse login(LoginRequest request) {
        // 认证用户名密码
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        String username = authentication.getName();
        // 生成JWT令牌
        String token = jwtTokenProvider.generateToken(username);
        // 令牌存入Redis（键：username，值：token，过期时间与JWT一致）
        redisTemplate.opsForValue().set(
                "token:" + username,
                token,
                jwtTokenProvider.getExpiration(),
                TimeUnit.MILLISECONDS
        );
        // 返回用户信息和令牌
        Users user = usersRepository.findByUsername(username).orElseThrow();
        return new LoginResponse(token, user.getUserId(), user.getUsername(), user.getRole().name());
    }

    // 刷新令牌
    @Override
    public String refreshToken(String oldToken) {
        // 验证旧令牌有效性
        if (!jwtTokenProvider.validateToken(oldToken)) {
            throw new BusinessException("旧令牌无效");
        }
        String username = jwtTokenProvider.extractUsername(oldToken);
        // 生成新令牌
        String newToken = jwtTokenProvider.generateToken(username);
        // 更新Redis中的令牌
        redisTemplate.opsForValue().set(
                "token:" + username,
                newToken,
                jwtTokenProvider.getExpiration(),
                TimeUnit.MILLISECONDS
        );
        return newToken;
    }

    // 退出登录
    @Override
    public void logout(String username) {
        redisTemplate.delete("token:" + username);
    }
}