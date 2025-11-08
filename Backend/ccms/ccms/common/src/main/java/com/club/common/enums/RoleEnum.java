// common/src/main/java/com/club/common/enums/RoleEnum.java
package com.club.common.enums;

import lombok.Getter;

/**
 * 用户角色枚举（避免硬编码）
 */
@Getter
public enum RoleEnum {
    ADMIN("admin", "Ⅰ级管理员（团委）"),
    CLUB_PRESIDENT("clubPresident", "Ⅱ级管理员（社团主席）"),
    MEMBER("member", "Ⅲ级用户（普通社员）");

    private final String code; // 存储在数据库中的值
    private final String desc; // 角色描述

    RoleEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 根据code获取枚举
    public static RoleEnum getByCode(String code) {
        for (RoleEnum role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        return null;
    }
}