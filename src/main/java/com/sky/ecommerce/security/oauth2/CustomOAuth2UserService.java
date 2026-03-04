package com.sky.ecommerce.security.oauth2;

import com.sky.ecommerce.domain.user.entity.Role;
import com.sky.ecommerce.domain.user.entity.User;
import com.sky.ecommerce.domain.user.repository.UserRepository;
import com.sky.ecommerce.security.userdetails.CustomUserDetails;
import com.sky.ecommerce.security.userdetails.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * OAuth2 로그인 처리 서비스
 * Spring이 카카오와 토큰 교환 후 자동으로 이 서비스를 호출함
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 부모 클래스가 카카오 API 호출 → 사용자 정보 Map 획득
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. 카카오 응답 파싱
        KakaoOAuth2UserInfo userInfo = new KakaoOAuth2UserInfo(oAuth2User.getAttributes());

        // Find or Create: provider + providerId로 조회, 없으면 신규 가입
        User user = userRepository.findByProviderAndProviderId("kakao", userInfo.getProviderId())
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(userInfo.getEmail())
                                .name(userInfo.getName())
                                .role(Role.USER)
                                .provider("kakao")
                                .providerId(userInfo.getProviderId())
                                .build()
                ));

        // OAuth2User + UserDetails 통합 구현체로 반환
        return new CustomUserDetails(
                new UserPrincipal(user.getId(), user.getEmail(), user.getRole().name(), null),
                oAuth2User.getAttributes());
    }
}
