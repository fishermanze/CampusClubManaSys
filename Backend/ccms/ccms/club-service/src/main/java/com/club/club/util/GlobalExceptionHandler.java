package com.club.club.util;

import com.club.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理业务异常
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        return Result.fail(e.getMessage());
    }

    // 处理参数校验异常
    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(javax.validation.ConstraintViolationException e) {
        return Result.fail(e.getConstraintViolations().iterator().next().getMessage());
    }
}