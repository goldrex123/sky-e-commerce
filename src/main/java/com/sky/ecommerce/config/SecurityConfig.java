package com.sky.ecommerce.config;

import com.sky.ecommerce.security.handler.CustomAccessDeniedHandler;
import com.sky.ecommerce.security.handler.CustomAuthenticationEntryPoint;
import com.sky.ecommerce.security.jwt.JwtAuthenticationFilter;
import com.sky.ecommerce.security.jwt.JwtProvider;
import com.sky.ecommerce.security.oauth2.CustomOAuth2UserService;
import com.sky.ecommerce.security.oauth2.OAuth2SuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

/**
 * Spring Security 핵심 설정
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // @PreAuthorize 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final StringRedisTemplate redisTemplate;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors((cors) -> cors.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("*"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        return configuration;
                    }
                }))

                // CSRF: JWT + Stateless 방식에선 불필요
                .csrf(AbstractHttpConfigurer::disable)

                // 세션: JWT 방식이므로 서버에서 세션 미생성
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 경로별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**", "/oauth2/**", "/login/oauth2/**",
                                "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/products/**").permitAll()  // 상품 조회는 인증 불필요
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")          // ADMIN만
                        .requestMatchers("/api/seller/**").hasRole("SELLER")        // SELLER + ADMIN (계층 적용)
                        .anyRequest().authenticated()                               // USER 이상
                )

                // OAuth2 소셜 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                )

                // 401/403 JSON 핸들러 등록
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 삽입
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider, redisTemplate),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * 권한 계층 정의: ADMIN > SELLER > USER
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("""
                ROLE_ADMIN > ROLE_SELLER
                ROLE_SELLER > ROLE_USER
                """);
    }

    /**
     * @PreAuthorize에 RoleHierarchy 적용
     * static 선언: BeanFactoryPostProcessor 단계에서 처리되므로 순환 의존성 방지
     */
    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler =
                new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
