package com.example.ccms.dto.response;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private int code = 200; // 默认成功状态码
    private String message = "success"; // 默认成功消息
    private T data; // 响应数据

    // 成功响应（无数据）
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>();
    }

    // 成功响应（带消息）
    public static <T> ApiResponse<T> success(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setMessage(message);
        return response;
    }

    // 成功响应（带数据）
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setData(data);
        return response;
    }

    // 成功响应（带消息和数据）
    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    // 错误响应（带状态码和消息）
    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}