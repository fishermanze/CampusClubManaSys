package com.club.auth.controller;

import com.club.auth.dto.LoginDTO;
import com.club.auth.service.UserService;
import com.club.auth.util.JwtUtils;
import com.club.common.entity.User;
import com.club.common.result.Result;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private UserService userService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        // 1. 校验请求参数
        if (loginDTO.getUsername() == null || loginDTO.getPassword() == null) {
            return Result.fail("用户名或密码不能为空");
        }

        // 2. 根据用户名查询用户
        User user = userService.getByUsername(loginDTO.getUsername());
        if (user == null) {
            return Result.fail("用户名不存在");
        }

        // 3. 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return Result.fail("密码错误");
        }

        // 4. 生成JWT令牌（确保用户ID为Long类型）
        String token = jwtUtils.generateToken(user.getId());

        // 5. 保存令牌到Redis（若需Redis校验，启用此行）
        jwtUtils.saveTokenToRedis(user.getId(), token);

        // 6. 构建返回数据（脱敏处理）
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUsername(user.getUsername());
        safeUser.setName(user.getName());
        safeUser.setCollege(user.getCollege());
        safeUser.setRole(user.getRole());
        safeUser.setHobby(user.getHobby());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", safeUser);

        return Result.success(data);
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        if (userId == null) {
            return Result.fail("用户ID不能为空");
        }
        // 从Redis删除令牌（注销逻辑）
        jwtUtils.removeTokenFromRedis(userId);
        return Result.success(null);
    }
}