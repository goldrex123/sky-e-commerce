package com.sky.ecommerce.security.userdetails;

import com.sky.ecommerce.domain.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security의 UserDetails 구현체
 * - 우리의 User 엔티티를 Security가 이해할 수 있는 형태로 감싸는 Adapter
 */
@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

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
        // Security에서 식별자로 사용 (우리는 email을 사용)
        return user.getEmail();
    }

    // 편의 메서드: JWT 발급 시 userId가 필요함
    public Long getUserId() {
        return user.getId();
    }
}
