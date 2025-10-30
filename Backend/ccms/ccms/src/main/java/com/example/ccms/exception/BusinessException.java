package com.example.ccms.exception;

import com.example.ccms.enums.ErrorCodeEnum;
import lombok.Getter;

/**
 * 业务异常类：用于处理可预见的业务错误（如用户不存在、参数错误等）
 */
@Getter
public class BusinessException extends RuntimeException {
    private final int code; // 错误码

    // 直接传入错误码和消息
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    // 通过错误码枚举传入（推荐）
    public BusinessException(ErrorCodeEnum errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }
}