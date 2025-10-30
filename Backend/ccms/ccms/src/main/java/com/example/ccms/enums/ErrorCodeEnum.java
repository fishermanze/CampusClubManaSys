package com.example.ccms.enums;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {
    // 用户模块
    USERNAME_EXIST(10002, "用户名已存在"),
    PHONE_EXIST(10003, "手机号已存在"),
    EMAIL_EXIST(10006, "邮箱已存在"),
    USER_NOT_FOUND(10001, "用户不存在"),
    PASSWORD_INCORRECT(10004, "密码错误"),
    USER_NOT_LOGGED_IN(10005, "用户未登录"),

    // 社团模块
    CLUB_NOT_FOUND(20001, "社团不存在"),
    CLUB_NAME_EXIST(20002, "社团名称已存在"),
    CLUB_RECRUIT_CLOSED(20003, "社团已停止招新"),
    CLUB_NOT_MANAGER(20004, "无社团管理权限"),

    // 申请模块
    APPLICATION_NOT_FOUND(30001, "申请记录不存在"),
    APPLICATION_REPEAT(30002, "已提交过申请，请勿重复提交"),
    APPLICATION_ALREADY_HANDLED(30003, "申请已处理，无法重复操作"),

    // 消息模块
    MESSAGE_NOT_FOUND(40001, "消息不存在"),

    // 系统模块
    SYSTEM_ERROR(50001, "系统内部错误"),
    PARAM_ERROR(50002, "参数格式错误");

    private final int code;
    private final String message;

    ErrorCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
