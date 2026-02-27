package com.sky.ecommerce.security.oauth2;

import java.util.Map;

/**
 * 카카오 OAuth2 응답에서 사용자 정보를 추출하는 클래스
 *
 * 카카오 응답 JSON 구조:
 * {
 *   "id": 12345678,
 *   "kakao_account": {
 *     "email": "user@example.com",
 *     "profile": {
 *       "nickname": "홍길동"
 *     }
 *   }
 * }
 */
public class KakaoOAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> profile;

    @SuppressWarnings("unchecked")
    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.profile = (Map<String, Object>) kakaoAccount.get("profile");
    }

    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    public String getEmail() {
        return String.valueOf(kakaoAccount.get("email"));
    }

    public String getName() {
        return String.valueOf(profile.get("nickname"));

    }
}
