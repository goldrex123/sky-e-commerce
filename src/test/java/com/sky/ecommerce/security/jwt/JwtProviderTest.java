package com.sky.ecommerce.security.jwt;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * JwtProvider 단위 테스트
 * - Spring Context 없이 순수 JWT 로직만 검증 (빠른 실행)
 */
class JwtProviderTest {

    // 테스트용 32자 이상 시크릿 키
    private static final String TEST_SECRET = "my-secret-key-for-jwt-must-be-at-least-32-characters";
    private static final long ACCESS_EXPIRY = 1_209_600_000L;   // 14일
    private static final long REFRESH_EXPIRY = 1_209_600_000L; // 14일

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(TEST_SECRET, ACCESS_EXPIRY, REFRESH_EXPIRY);
    }

    // ─────────────────────────────────────────────────
    // Access Token 생성 및 Claim 검증
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("Access Token 생성")
    class GenerateAccessToken {

        @Test
        @DisplayName("userId, email, role이 정확히 담겨야 한다")
        void shouldContainCorrectClaims() {
            // given
            Long userId = 3L;
            String email = "xxx3@naver.com";
            String role = "ROLE_ADMIN";

            // when
            String token = jwtProvider.generateAccessToken(userId, email, role);

            System.out.println("token = " + token);

            // then
            assertThat(token).isNotBlank();
            assertThat(jwtProvider.getUserId(token)).isEqualTo(userId);
            assertThat(jwtProvider.getEmail(token)).isEqualTo(email);
            assertThat(jwtProvider.getRole(token)).isEqualTo(role);
        }

        @Test
        @DisplayName("issuedAt과 expiration이 설정되어야 한다")
        void shouldHaveIssuedAtAndExpiration() {
            // given
            String token = jwtProvider.generateAccessToken(1L, "test@example.com", "ROLE_USER");

            // when
            Claims claims = jwtProvider.parseClaims(token);

            // then
            assertThat(claims.getIssuedAt()).isNotNull();
            assertThat(claims.getExpiration()).isNotNull();
            assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
        }
    }

    // ─────────────────────────────────────────────────
    // Refresh Token 생성 및 Claim 검증
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("Refresh Token 생성")
    class GenerateRefreshToken {

        @Test
        @DisplayName("userId만 포함하며 email/role은 없어야 한다 (최소 정보 원칙)")
        void shouldContainOnlyUserId() {
            // given
            Long userId = 42L;

            // when
            String token = jwtProvider.generateRefreshToken(userId);

            // then
            assertThat(jwtProvider.getUserId(token)).isEqualTo(userId);

            Claims claims = jwtProvider.parseClaims(token);
            assertThat(claims.get("email")).isNull();
            assertThat(claims.get("role")).isNull();
        }
    }

    // ─────────────────────────────────────────────────
    // 토큰 유효성 검증
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("토큰 유효성 검증")
    class ValidateToken {

        @Test
        @DisplayName("유효한 토큰은 true를 반환해야 한다")
        void shouldReturnTrueForValidToken() {
            String token = jwtProvider.generateAccessToken(1L, "test@example.com", "ROLE_USER");
            assertThat(jwtProvider.validateToken(token)).isTrue();
        }

        @Test
        @DisplayName("위변조된 토큰은 false를 반환해야 한다")
        void shouldReturnFalseForTamperedToken() {
            String token = jwtProvider.generateAccessToken(1L, "test@example.com", "ROLE_USER");
            String tampered = token + "malicious";

            assertThat(jwtProvider.validateToken(tampered)).isFalse();
        }

        @Test
        @DisplayName("빈 문자열 토큰은 false를 반환해야 한다")
        void shouldReturnFalseForEmptyToken() {
            assertThat(jwtProvider.validateToken("")).isFalse();
        }

        @Test
        @DisplayName("만료된 토큰은 false를 반환해야 한다")
        void shouldReturnFalseForExpiredToken() throws InterruptedException {
            // expiry=0 으로 즉시 만료 토큰 생성
            JwtProvider shortLivedProvider = new JwtProvider(TEST_SECRET, 0L, 0L);
            String token = shortLivedProvider.generateAccessToken(1L, "test@example.com", "ROLE_USER");

            Thread.sleep(1); // 만료 확실히 대기

            assertThat(shortLivedProvider.validateToken(token)).isFalse();
        }

        @Test
        @DisplayName("다른 시크릿으로 서명된 토큰은 false를 반환해야 한다")
        void shouldReturnFalseForDifferentSecretToken() {
            JwtProvider otherProvider = new JwtProvider(
                    "other-secret-key-completely-different-1234567890", ACCESS_EXPIRY, REFRESH_EXPIRY);

            String token = otherProvider.generateAccessToken(1L, "test@example.com", "ROLE_USER");

            assertThat(jwtProvider.validateToken(token)).isFalse();
        }
    }

    // ─────────────────────────────────────────────────
    // parseClaims 직접 호출 예외 검증
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("parseClaims 예외 처리")
    class ParseClaims {

        @Test
        @DisplayName("잘못된 토큰 형식은 예외를 던져야 한다")
        void shouldThrowForMalformedToken() {
            assertThatThrownBy(() -> jwtProvider.parseClaims("not.a.jwt"))
                    .isInstanceOf(Exception.class);
        }
    }
}
