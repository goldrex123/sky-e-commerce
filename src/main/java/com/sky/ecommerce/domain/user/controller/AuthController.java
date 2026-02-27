package com.sky.ecommerce.domain.user.controller;

import com.sky.ecommerce.domain.user.dto.LoginRequest;
import com.sky.ecommerce.domain.user.dto.SignupRequest;
import com.sky.ecommerce.domain.user.dto.TokenResponse;
import com.sky.ecommerce.domain.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 API 컨트롤러
 * SecurityConfig에서 /api/auth/** 는 permitAll() 처리됨
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/signup
    @PostMapping("/signup")
    public ResponseEntity<TokenResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.signup(request));
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // POST /api/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String bearerToken) {
        if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }
        authService.logout(bearerToken.substring(7));
        return ResponseEntity.noContent().build();
    }

    // POST /api/auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody String refreshToken) {
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }
}
