package com.example.ccms.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举：统一管理系统错误标识与描述
 * 规则：10xxx=用户相关，20xxx=社团相关，30xxx=申请相关，40xxx=消息相关，50xxx=系统相关
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 通用错误
    SYSTEM_ERROR(50000, "系统繁忙，请稍后重试"),
    PARAM_ERROR(50001, "参数格式不正确"),

    // 用户相关错误
    USER_NOT_FOUND(10001, "用户不存在"),
    USERNAME_DUPLICATE(10002, "用户名已被占用"),
    PHONE_DUPLICATE(10003, "手机号已被绑定"),
    EMAIL_DUPLICATE(10004, "邮箱已被绑定"),
    PASSWORD_INCORRECT(10005, "密码不正确"),
    NO_PERMISSION(10006, "无权限执行该操作"),

    // 社团相关错误
    CLUB_NOT_FOUND(20001, "社团不存在"),
    CLUB_NAME_DUPLICATE(20002, "社团名称已存在"),
    CLUB_RECRUIT_CLOSED(20003, "该社团已关闭招新"),
    CLUB_MEMBER_FULL(20004, "社团人数已达上限"),

    // 申请相关错误
    APPLICATION_NOT_FOUND(30001, "申请记录不存在"),
    APPLICATION_DUPLICATE(30002, "已提交申请，请等待审核"),
    APPLICATION_ALREADY_HANDLED(30003, "该申请已处理"),

    // 消息相关错误
    MESSAGE_NOT_FOUND(40001, "消息不存在"),
    MESSAGE_NO_PERMISSION(40002, "无权操作该消息");

    private final int code;       // 错误码
    private final String message; // 错误描述
}