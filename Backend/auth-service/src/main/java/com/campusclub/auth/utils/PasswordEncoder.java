package com.campusclub.auth.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

    @Value("${password.salt}")
    private String salt;

    /**
     * 加密密码
     */
    public String encode(String password) {
        // 密码加盐并使用SHA256加密
        String saltedPassword = password + salt;
        return DigestUtils.sha256Hex(saltedPassword);
    }

    /**
     * 验证密码
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        String saltedRawPassword = rawPassword + salt;
        String hashedPassword = DigestUtils.sha256Hex(saltedRawPassword);
        return hashedPassword.equals(encodedPassword);
    }
}