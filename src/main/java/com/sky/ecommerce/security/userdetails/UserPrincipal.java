package com.sky.ecommerce.security.userdetails;

/**
 * Security Context에 저장되는 인증 주체 DTO
 * - 엔티티 대신 순수 데이터만 보유
 * - password: DB 로그인 시에만 사용, JWT 필터 경로에서는 null
 */
public record UserPrincipal(Long userId, String email, String role, String password) {

    /** JWT 클레임에서 생성 시 사용 (패스워드 불필요) */
    public static UserPrincipal fromToken(Long userId, String email, String role) {
        return new UserPrincipal(userId, email, role, null);
    }
}
