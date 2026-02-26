package com.sky.ecommerce.domain.user.entity;

/**
 * 사용자 권한 열거형
 * Spring Security의 GrantedAuthority로 사용됨
 */
public enum Role {
    USER,   // 일반 사용자
    ADMIN   // 관리자
}
