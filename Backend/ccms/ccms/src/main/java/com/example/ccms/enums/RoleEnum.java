package com.example.ccms.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {
    USER("普通用户"),
    ADMIN("管理员"),
    MANAGER("社团管理者");

    private final String desc;

    RoleEnum(String desc) {
        this.desc = desc;
    }
}