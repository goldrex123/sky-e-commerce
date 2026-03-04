package com.sky.ecommerce.domain.admin.controller;

import com.sky.ecommerce.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 관리자 전용 데모 컨트롤러
 * 경로 기반 + 메서드 기반 이중 보호 적용
 */
@Tag(name = "Admin", description = "관리자 전용 API (ADMIN만 접근 가능)")
@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")  // 메서드 레벨 2차 방어
public class AdminController {

    @Operation(summary = "관리자 대시보드", description = "ADMIN 권한 필요. USER/SELLER 접근 시 403 반환")
    @GetMapping("/dashboard")
    public ApiResponse<Map<String, String>> dashboard() {
        return ApiResponse.success(Map.of(
                "message", "관리자 대시보드입니다",
                "access", "ADMIN 전용"
        ));
    }

    @Operation(summary = "전체 회원 조회", description = "ADMIN 권한 필요")
    @GetMapping("/users")
    public ApiResponse<Map<String, String>> listUsers() {
        return ApiResponse.success(Map.of(
                "message", "전체 회원 목록 (데모)"
        ));
    }
}
