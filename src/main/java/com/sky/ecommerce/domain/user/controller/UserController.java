package com.sky.ecommerce.domain.user.controller;

import com.sky.ecommerce.common.ApiResponse;
import com.sky.ecommerce.security.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 일반 사용자 데모 컨트롤러
 * anyRequest().authenticated() 구간 → USER/SELLER/ADMIN 모두 접근 가능
 */
@Tag(name = "User", description = "인증된 사용자 API (로그인 필요)")
@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 정보와 권한을 반환합니다")
    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ApiResponse.success(Map.of(
                "userId", userDetails.getUserId(),
                "userName", userDetails.getUsername(),
                "role", userDetails.getAuthorities()
        ));
    }
}
