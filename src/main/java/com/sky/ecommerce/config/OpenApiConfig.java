package com.sky.ecommerce.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Springdoc OpenAPI 전역 설정
 * - JWT Bearer 인증 스키마 등록 (Swagger UI Authorize 버튼 활성화)
 * - API 기본 정보(제목, 버전, 연락처) 정의
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Ecommerce API",
                version = "1.0",
                description = "이커머스 백엔드 REST API 문서",
                contact = @Contact(name = "Dev Team")
        )
)
@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "로그인 후 발급받은 Access Token을 입력하세요. (Bearer 접두어 불필요)"
)
public class OpenApiConfig {
}
