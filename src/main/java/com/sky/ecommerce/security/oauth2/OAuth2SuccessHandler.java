package com.sky.ecommerce.security.oauth2;

import com.sky.ecommerce.security.jwt.JwtProvider;
import com.sky.ecommerce.security.userdetails.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

/**
 * OAuth2 로그인 성공 시 JWT 발급 핸들러
 *
 * 보안 전략:
 * - Access Token  → URL Fragment(#)로 프론트에 전달 (서버 로그에 남지 않음)
 * - Refresh Token → HttpOnly Cookie로 전달 (JS 접근 차단, XSS 방어)
 */
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String accessToken = jwtProvider.generateAccessToken(
                userDetails.getUserId(),
                userDetails.getUsername(),
                userDetails.getUser().getRole().name()
        );
        String refreshToken = jwtProvider.generateRefreshToken(userDetails.getUserId());

        // Redis에 Refresh Token 저장
        redisTemplate.opsForValue().set(
                "refresh:" + userDetails.getUserId(),
                refreshToken,
                Duration.ofDays(14)
        );

        // Refresh Token → HttpOnly Cookie로 전달
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)               // JS 접근 차단 (XSS 방어)
                .secure(true)                 // HTTPS 환경에서만 전송
                .path("/api/auth")            // 토큰 재발급 경로에서만 쿠키 전송
                .maxAge(Duration.ofDays(14))
                .sameSite("Lax")              // CSRF 방어
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        // Access Token → URL Fragment로 프론트에 리다이렉트
        // Fragment(#)는 서버로 전송되지 않아 서버 로그에 토큰이 남지 않음
        String redirectUrl = frontendUrl + "/oauth2/callback#access_token=" + accessToken;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
