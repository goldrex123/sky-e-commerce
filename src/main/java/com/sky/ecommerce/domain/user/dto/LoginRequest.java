package com.sky.ecommerce.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 로그인 요청 DTO
 */
public record LoginRequest(

        @NotBlank @Email
        @Schema(description = "로그인 이메일", example = "xxx@naver.com")
        String email,

        @NotBlank
        @Schema(description = "비밀번호", example = "qwer1234")
        String password
) {}
