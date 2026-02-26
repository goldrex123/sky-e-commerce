package com.sky.ecommerce.domain.user.repository;

import com.sky.ecommerce.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자 데이터 접근 계층
 */
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 조회 (일반 로그인 시 사용)
    Optional<User> findByEmail(String email);

    // 소셜 로그인 제공자 + 제공자 ID로 사용자 조회 (OAuth2 로그인 시 사용)
    Optional<User> findByProviderAndProviderId(String provider, String providerId);

    // 이메일 중복 체크
    boolean existsByEmail(String email);
}
