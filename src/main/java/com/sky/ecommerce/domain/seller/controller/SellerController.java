package com.sky.ecommerce.domain.seller.controller;

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
 * 판매자 전용 데모 컨트롤러
 * RoleHierarchy에 의해 ADMIN도 접근 허용 (ADMIN > SELLER > USER)
 */
@Tag(name = "Seller", description = "판매자 전용 API (SELLER 이상 접근 가능)")
@SecurityRequirement(name = "JWT")
@RestController
@RequestMapping("/api/seller")
@PreAuthorize("hasRole('SELLER')")  // RoleHierarchy 적용 → ADMIN도 통과
public class SellerController {

    @Operation(summary = "판매자 대시보드", description = "SELLER/ADMIN 접근 가능. USER 접근 시 403 반환")
    @GetMapping("/dashboard")
    public ApiResponse<Map<String, String>> dashboard() {
        return ApiResponse.success(Map.of(
                "message", "판매자 대시보드입니다",
                "access", "SELLER + ADMIN"
        ));
    }

    @Operation(summary = "상품 관리", description = "SELLER/ADMIN 접근 가능")
    @GetMapping("/products")
    public ApiResponse<Map<String, String>> manageProducts() {
        return ApiResponse.success(Map.of(
                "message", "판매자 상품 관리 (데모)"
        ));
    }
}
