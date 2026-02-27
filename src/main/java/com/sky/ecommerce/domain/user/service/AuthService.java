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
import java.util.Date;

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
    private static final String BLACKLIST_TOKEN_PREFIX = "blacklist:";
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
     * 로그아웃
     * - Access Token → Redis 블랙리스트 등록 (남은 만료시간 동안 차단)
     * - Refresh Token → Redis에서 삭제
     */
    public void logout(String accessToken) {
        Date accessTokenExpiration = jwtProvider.parseClaims(accessToken).getExpiration();
        long remainExpiration = accessTokenExpiration.getTime() - System.currentTimeMillis();

        if (remainExpiration > 0) {
            redisTemplate.opsForValue().set(BLACKLIST_TOKEN_PREFIX + accessToken, "logout", Duration.ofMillis(remainExpiration));
        }

        Long userId = jwtProvider.getUserId(accessToken);
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
    }

    /**
     * Access Token 재발급 (RT Rotation)
     * - 기존 Refresh Token 검증 → 새 AT + 새 RT 발급 → 기존 RT 폐기
     */
    public TokenResponse refresh(String refreshToken) {
        // RT 유효성 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다");
        }

        Long userId = jwtProvider.getUserId(refreshToken);

        // Redis에 저장된 RT와 일치 여부 확인 (탈취된 RT 재사용 방지)
        String storedToken = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId);
        if (!refreshToken.equals(storedToken)) {
            throw new IllegalArgumentException("이미 사용되었거나 만료된 Refresh Token입니다");
        }

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // RT Rotation: 기존 RT 폐기 후 새 토큰 세트 발급
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
