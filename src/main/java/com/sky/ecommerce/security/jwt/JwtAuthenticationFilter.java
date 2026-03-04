package com.sky.ecommerce.security.jwt;

import com.sky.ecommerce.common.exception.ErrorCode;
import com.sky.ecommerce.security.userdetails.CustomUserDetails;
import com.sky.ecommerce.security.userdetails.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 매 요청마다 JWT를 검증하고 SecurityContext에 인증 정보를 등록하는 필터
 * OncePerRequestFilter: 요청당 정확히 1번만 실행 보장
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null) {
            if (!jwtProvider.validateToken(token) || isBlacklisted(token)) {
                // 유효하지 않거나 블랙리스트에 등록된 토큰 → EntryPoint에서 A007로 처리
                request.setAttribute("exception", ErrorCode.INVALID_ACCESS_TOKEN);
            } else {
                setAuthentication(token);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Redis 블랙리스트 등록 여부 확인 (로그아웃된 토큰 차단)
     */
    private boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token));
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
     * DB 조회 없이 JWT 클레임만으로 UserPrincipal을 구성하고 SecurityContext에 등록
     */
    private void setAuthentication(String token) {
        Long userId = jwtProvider.getUserId(token);
        String email = jwtProvider.getEmail(token);
        String role = jwtProvider.getRole(token);

        CustomUserDetails userDetails = new CustomUserDetails(UserPrincipal.fromToken(userId, email, role));

        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        log.debug("JWT 인증 완료 userId - {}, email - {},  role - {}", userId, email, role);
    }
}
