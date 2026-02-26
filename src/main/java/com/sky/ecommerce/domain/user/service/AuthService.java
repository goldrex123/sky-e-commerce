package com.sky.ecommerce.domain.user.service;

import com.sky.ecommerce.domain.user.dto.LoginRequest;
import com.sky.ecommerce.domain.user.dto.SignupRequest;
import com.sky.ecommerce.domain.user.dto.TokenResponse;
import com.sky.ecommerce.domain.user.entity.Role;
import com.sky.ecommerce.domain.user.entity.User;
import com.sky.ecommerce.domain.user.repository.UserRepository;
import com.sky.ecommerce.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

/**
 * 인증 관련 비즈니스 로직 (회원가입, 로그인, 로그아웃, 토큰 재발급)
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(14);

    /**
     * 회원가입
     */
    @Transactional
    public TokenResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .role(Role.USER)
                .build();

        userRepository.save(user);
        return issueTokens(user);
    }

    /**
     * 로그인
     */
    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호를 확인하세요.");
        }

        return issueTokens(user);
    }

    /**
     * 토큰 발급 및 Redis 저장 (내부 공통 메서드)
     */
    private TokenResponse issueTokens(User user) {
        String accessToken = jwtProvider.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        // Redis에 Refresh Token 저장: "refresh:{userId}" → refreshToken
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + user.getId(),
                refreshToken,
                REFRESH_TOKEN_TTL
        );

        return new TokenResponse(accessToken, refreshToken);
    }
}
