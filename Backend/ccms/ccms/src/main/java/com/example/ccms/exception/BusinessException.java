package com.example.ccms.exception;

// 业务异常基类
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}