package com.sky.ecommerce.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 토큰 발급 응답 DTO
 * 로그인/회원가입/토큰재발급 시 공통으로 사용
 */
public record TokenResponse(
        @Schema(description = "Access Token (30분 유효)", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIn0.abc")
        String accessToken,
        @Schema(description = "Refresh Token (14일 유효)", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIn0.xyz")
        String refreshToken
) {}
