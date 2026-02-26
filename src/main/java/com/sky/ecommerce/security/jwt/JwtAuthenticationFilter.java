package com.sky.ecommerce.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * 매 요청마다 JWT를 검증하고 SecurityContext에 인증 정보를 등록하는 필터
 * OncePerRequestFilter: 요청당 정확히 1번만 실행 보장
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);

        // 토큰이 존재하고 유효한 경우에만 SecurityContext에 인증 정보 등록
        if (token != null && jwtProvider.validateToken(token)) {
            setAuthentication(token);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Authorization 헤더에서 Bearer 토큰 추출
     * "Bearer eyJhbGc..." → "eyJhbGc..."
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    /**
     * SecurityContextHolder에 인증 정보 등록
     */
    private void setAuthentication(String token) {
        Long userId = jwtProvider.getUserId(token);
        String email = jwtProvider.getEmail(token);
        String role = jwtProvider.getRole(token);

        // UsernamePasswordAuthenticationToken: Security의 기본 Authentication 구현체
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        email,                                                // principal (식별자)
                        null,                                                 // credentials (비밀번호, JWT 방식에선 불필요)
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))  // authorities (권한)
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("JWT 인증 완료 - userId: {}, email: {}", userId, email);
    }
}
