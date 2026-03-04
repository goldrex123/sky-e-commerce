package com.sky.ecommerce.common.exception;

/**
 * 인증/인가 관련 도메인 예외
 * A001 ~ A009 계열 ErrorCode와 함께 사용
 */
public class AuthException extends BusinessException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
