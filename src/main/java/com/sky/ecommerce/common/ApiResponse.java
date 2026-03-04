package com.sky.ecommerce.common;

import com.sky.ecommerce.common.exception.ErrorCode;

/**
 * 공통 API 응답 래퍼
 * 모든 API 응답을 { success, code, message, data } 형태로 통일
 */
public record ApiResponse<T>(
        boolean success,
        String code,
        String message,
        T data
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, ErrorCode.SUCCESS.getCode(), "성공", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, ErrorCode.SUCCESS.getCode(), message, data);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(false, errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String message) {
        return new ApiResponse<>(false, errorCode.getCode(), message, null);
    }

    /** @deprecated ErrorCode 기반 메서드 사용 권장 */
    @Deprecated
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, "C002", message, null);
    }
}
