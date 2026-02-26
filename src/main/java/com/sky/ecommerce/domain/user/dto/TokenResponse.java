package com.sky.ecommerce.domain.user.dto;

/**
 * 토큰 발급 응답 DTO
 * 로그인/회원가입/토큰재발급 시 공통으로 사용
 */
public record TokenResponse(
        String accessToken,
        String refreshToken
) {}
