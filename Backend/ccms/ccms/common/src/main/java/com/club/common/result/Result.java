// common/src/main/java/com/club/common/result/Result.java
package com.club.common.result;

import lombok.Data;

/**
 * 全局统一响应实体
 * 所有接口的返回格式都必须使用此类，确保前后端交互格式一致
 */
@Data
public class Result<T> {
    // 状态码：200=成功，401=未认证，500=服务器错误，其他可自定义
    private Integer code;

    // 响应信息：成功时返回"操作成功"，失败时返回具体原因
    private String message;

    // 响应数据：成功时返回具体业务数据，失败时为null
    private T data;

    // 成功响应（带数据）
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    // 成功响应（无数据）
    public static <T> Result<T> success() {
        return success(null);
    }

    // 失败响应（自定义错误信息）
    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    // 未认证响应（登录失效或未登录）
    public static <T> Result<T> unAuth() {
        Result<T> result = new Result<>();
        result.setCode(401);
        result.setMessage("请先登录");
        result.setData(null);
        return result;
    }

    // 自定义状态码和信息（特殊场景用）
    public static <T> Result<T> build(Integer code, String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
}