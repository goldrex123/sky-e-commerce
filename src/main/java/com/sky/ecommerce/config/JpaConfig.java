package com.sky.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/**
 * JPA Auditing 활성화 설정
 * SecurityConfig와 분리하는 이유: Security 슬라이스 테스트 시 충돌 방지
 */
@Configuration
@EnableJpaAuditing
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class JpaConfig {
}
