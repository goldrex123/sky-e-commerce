package com.sky.ecommerce.domain.user.controller;

import com.sky.ecommerce.domain.user.dto.LoginRequest;
import com.sky.ecommerce.domain.user.dto.SignupRequest;
import com.sky.ecommerce.domain.user.dto.TokenResponse;
import com.sky.ecommerce.domain.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Auth", description = "인증 관련 API (회원가입/로그인/로그아웃/토큰재발급)")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원 가입 API")
    @ApiResponses(
            @ApiResponse(responseCode = "201", description = "회원가입 성공")
    )
    public ResponseEntity<TokenResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.signup(request));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 API")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "로그인 성공")
    )
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API")
    @SecurityRequirement(name = "JWT")
    @ApiResponses(
            @ApiResponse(responseCode = "204", description = "로그아웃 성공")
    )
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String bearerToken) {
        if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }
        authService.logout(bearerToken.substring(7));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    @Operation(summary = "AccessToken 재발급 API")
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "AccessToken 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "RefreshToken 검증 실패로 인한 AccessToken 재발급 실패")
    })
    public ResponseEntity<TokenResponse> refresh(@RequestBody String refreshToken) {
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }
}
