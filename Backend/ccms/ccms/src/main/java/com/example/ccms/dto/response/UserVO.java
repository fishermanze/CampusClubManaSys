package com.example.ccms.dto.response;

import lombok.Data;

/**
 * 用户视图：展示用户信息（脱敏处理，不包含密码等敏感字段）
 */
@Data
public class UserVO {

    private Long id; // 用户ID
    private String username; // 登录账号
    private String realName; // 真实姓名（展示用）
    private String avatar; // 头像URL（可选）
    private String phone; // 手机号（可选，已验证）
    private String email; // 邮箱（可选，已验证）
    private String role; // 角色等级（LEVEL_1/2/3）
}