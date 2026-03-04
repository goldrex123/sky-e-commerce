package com.sky.ecommerce.common.exception;

/**
 * 리소스 미존재 예외 (HTTP 404)
 * A006 등 NOT_FOUND 계열 ErrorCode와 함께 사용
 */
public class NotFoundException extends BusinessException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
