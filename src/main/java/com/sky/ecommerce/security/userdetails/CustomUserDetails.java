package com.sky.ecommerce.security.userdetails;

import com.sky.ecommerce.domain.user.entity.User;
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

    private final User user;
    private final Map<String, Object> attributes; // OAuth2 로그인 시에만 사용

    // 일반 로그인용 생성자
    public CustomUserDetails(User user) {
        this.user = user;
        this.attributes = null;
    }

    // OAuth2 로그인용 생성자
    public CustomUserDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // OAuth2User 인터페이스 구현
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        // OAuth2User의 식별자 (카카오 id)
        return user.getEmail();
    }

    // 편의 메서드
    public Long getUserId() {
        return user.getId();
    }
}
