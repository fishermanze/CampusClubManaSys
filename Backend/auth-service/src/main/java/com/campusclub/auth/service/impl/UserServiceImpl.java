package com.campusclub.auth.service.impl;

import com.campusclub.auth.dto.LoginRequest;
import com.campusclub.auth.dto.LoginResponse;
import com.campusclub.auth.dto.RegisterRequest;
import com.campusclub.auth.dto.ApiResponse;
import com.campusclub.auth.entity.User;
import com.campusclub.auth.repository.UserRepository;
import com.campusclub.auth.service.UserService;
import com.campusclub.auth.utils.JwtTokenProvider;
import com.campusclub.auth.utils.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${verification.code.expiration}")
    private long verificationCodeExpiration;

    @Override
    public ApiResponse<LoginResponse> login(LoginRequest request) {
        // 验证用户名和密码
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ApiResponse.error(401, "用户名或密码错误");
        }

        // 检查用户是否被禁用
        if (!user.isEnabled()) {
            return ApiResponse.error(403, "账户已被禁用，请联系管理员");
        }

        // 生成令牌
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername(), user.getRole(), user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        // 将用户信息存入Redis，用于分布式会话管理
        String tokenKey = "token:" + user.getId() + ":" + accessToken;
        redisTemplate.opsForValue().set(tokenKey, user.getId(), expiration, TimeUnit.MILLISECONDS);

        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(expiration / 1000);

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setName(user.getRealName());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setRole(user.getRole());
        userInfo.setEmail(user.getEmail());
        userInfo.setPhone(user.getPhone());
        userInfo.setJoinedClubs(0); // 后续从用户服务获取
        userInfo.setParticipatedActivities(0); // 后续从统计服务获取

        response.setUser(userInfo);

        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<String> register(RegisterRequest request) {
        // 验证密码一致性
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ApiResponse.error(400, "两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            return ApiResponse.error(400, "用户名已存在");
        }


        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setRole("STUDENT"); // 默认角色为学生
        user.setEnabled(true);
        user.setAvatar("https://i.pravatar.cc/150?img=" + UUID.randomUUID().toString().substring(0, 6)); // 随机头像
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        userRepository.save(user);


        return ApiResponse.success("注册成功");
    }

    @Override
    public ApiResponse<String> logout(String token) {
        if (!StringUtils.hasText(token)) {
            return ApiResponse.error(400, "token不能为空");
        }

        try {
            // 从token中获取用户ID
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            String tokenKey = "token:" + userId + ":" + token;
            redisTemplate.delete(tokenKey);
            return ApiResponse.success("退出成功");
        } catch (Exception e) {
            return ApiResponse.error(400, "无效的token");
        }
    }

    @Override
    public ApiResponse<LoginResponse> refreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            return ApiResponse.error(400, "refresh token不能为空");
        }

        try {
            // 验证refresh token
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                return ApiResponse.error(401, "无效的refresh token");
            }

            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            // 生成新的访问令牌
            String newAccessToken = jwtTokenProvider.generateAccessToken(user.getUsername(), user.getRole(), user.getId());

            // 更新Redis中的token
            String oldTokenKey = "token:" + user.getId() + ":" + refreshToken;
            redisTemplate.delete(oldTokenKey);
            String newTokenKey = "token:" + user.getId() + ":" + newAccessToken;
            redisTemplate.opsForValue().set(newTokenKey, user.getId(), expiration, TimeUnit.MILLISECONDS);

            LoginResponse response = new LoginResponse();
            response.setAccessToken(newAccessToken);
            response.setRefreshToken(refreshToken); // 可以选择是否更新refresh token
            response.setExpiresIn(expiration / 1000);

            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setName(user.getRealName());
            userInfo.setAvatar(user.getAvatar());
            userInfo.setRole(user.getRole());
            userInfo.setEmail(user.getEmail());
            userInfo.setPhone(user.getPhone());

            response.setUser(userInfo);

            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(401, "刷新令牌失败: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<String> sendVerificationCode(String phone) {
        // 生成验证码
        String code = String.format("%06d", (int) (Math.random() * 900000) + 100000);

        // 存入Redis，设置过期时间
        String key = "verification_code:" + phone;
        redisTemplate.opsForValue().set(key, code, verificationCodeExpiration, TimeUnit.MINUTES);

        // 这里应该调用短信服务发送验证码
        // 目前模拟发送成功
        System.out.println("验证码: " + code + " 已发送至手机: " + phone);

        return ApiResponse.success("验证码已发送");
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public ApiResponse<String> changePassword(Long userId, String oldPassword, String newPassword, String confirmPassword) {
        // 验证新密码一致性
        if (!newPassword.equals(confirmPassword)) {
            return ApiResponse.error(400, "两次输入的新密码不一致");
        }

        // 获取用户信息
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ApiResponse.error(404, "用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ApiResponse.error(400, "旧密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ApiResponse.success("密码修改成功");
    }

    @Override
    public ApiResponse<User> updateUserInfo(User user) {
        // 验证用户是否存在
        User existingUser = userRepository.findById(user.getId()).orElse(null);
        if (existingUser == null) {
            return ApiResponse.error(404, "用户不存在");
        }

        // 更新用户信息
        existingUser.setRealName(user.getRealName());
        existingUser.setEmail(user.getEmail());
        if (StringUtils.hasText(user.getAvatar())) {
            existingUser.setAvatar(user.getAvatar());
        }

        userRepository.save(existingUser);

        return ApiResponse.success(existingUser);
    }

    @Override
    public ApiResponse<String> resetPassword(String phone, String verificationCode, String newPassword, String confirmPassword) {
        // 验证新密码一致性
        if (!newPassword.equals(confirmPassword)) {
            return ApiResponse.error(400, "两次输入的新密码不一致");
        }

        // 验证验证码
        String codeKey = "verification_code:" + phone;
        String storedCode = (String) redisTemplate.opsForValue().get(codeKey);
        if (storedCode == null || !storedCode.equals(verificationCode)) {
            return ApiResponse.error(400, "验证码错误或已过期");
        }

        // 查找用户
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("未找到该手机号对应的用户"));

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 删除已使用的验证码
        redisTemplate.delete(codeKey);

        return ApiResponse.success("密码重置成功");
    }
}