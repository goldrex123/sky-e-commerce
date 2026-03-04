package com.sky.ecommerce.common.exception;

import lombok.Getter;

/**
 * 모든 도메인 비즈니스 예외의 베이스 클래스
 * ErrorCode를 포함해 핸들러에서 일관된 응답 생성 가능
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
