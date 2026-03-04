package com.sky.ecommerce.security.userdetails;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Spring Security의 UserDetails + OAuth2User 통합 구현체
 * - 일반 로그인: UserDetails로 동작 (attributes = null)
 * - OAuth2 로그인: OAuth2User로 동작 (attributes = 카카오 응답)
 */
@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final UserPrincipal principal;
    private final Map<String, Object> attributes; // OAuth2 로그인 시에만 사용

    // 일반 로그인 / JWT 필터용 생성자
    public CustomUserDetails(UserPrincipal principal) {
        this.principal = principal;
        this.attributes = null;
    }

    // OAuth2 로그인용 생성자
    public CustomUserDetails(UserPrincipal principal, Map<String, Object> attributes) {
        this.principal = principal;
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + principal.role()));
    }

    @Override
    public String getPassword() {
        // 일반 로그인 시 DB에서 로드한 경우에만 값 존재, JWT 필터 경로에서는 null
        return principal.password();
    }

    @Override
    public String getUsername() {
        return principal.email();
    }

    // OAuth2User 인터페이스 구현
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return principal.email();
    }

    // 편의 메서드
    public Long getUserId() {
        return principal.userId();
    }

    public String getRole() {
        return principal.role();
    }
}
