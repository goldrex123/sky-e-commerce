package com.sky.ecommerce.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 회원가입 요청 DTO
 */
public record SignupRequest(

        @NotBlank(message = "이메일을 입력해주세요")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        @Schema(description = "회원가입 이메일", example = "xxx@naver.com")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
        @Schema(description = "비밀번호", example = "qwer1234")
        String password,

        @NotBlank(message = "이름을 입력해주세요")
        @Schema(description = "이름", example = "홍길동")
        String name
) {
}
