package com.sky.ecommerce.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 전역 에러 코드 정의
 * - S: 성공
 * - C: 공통 (Common)
 * - A: 인증/인가 (Auth)
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 성공
    SUCCESS(HttpStatus.OK, "S000", "성공"),

    // 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C001", "입력값이 올바르지 않습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 내부 오류가 발생했습니다"),

    // 인증/인가 (A)
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "A001", "이미 사용 중인 이메일입니다"),
    USER_NOT_FOUND_BY_EMAIL(HttpStatus.UNAUTHORIZED, "A002", "가입되지 않은 이메일입니다"),
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "A003", "비밀번호를 확인하세요"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A004", "유효하지 않은 Refresh Token입니다"),
    REUSED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A005", "이미 사용되었거나 만료된 Refresh Token입니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "A006", "사용자를 찾을 수 없습니다"),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "A007", "유효하지 않거나 만료된 Access Token입니다"),
    ACCESS_TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, "A008", "인증이 필요합니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A009", "접근 권한이 없습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
