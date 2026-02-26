package com.sky.ecommerce.domain.user.entity;

import com.sky.ecommerce.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 엔티티
 * - 일반 로그인: email + password 사용
 * - 소셜 로그인: provider + providerId 사용, password는 null
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    // 소셜 로그인 사용자는 비밀번호 없음
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // OAuth2 소셜 로그인 정보 (일반 로그인 시 null)
    private String provider;    // ex) "kakao"
    private String providerId;  // ex) "12345678"

    // updatePassword(String encodedPassword) 메서드를 작성해보세요.
    // 힌트: 기존 password 필드를 새 값으로 교체하는 간단한 메서드입니다.
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
