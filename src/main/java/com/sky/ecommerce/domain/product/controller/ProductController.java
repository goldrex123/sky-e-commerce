package com.sky.ecommerce.domain.product.controller;

import com.sky.ecommerce.common.ApiResponse;
import com.sky.ecommerce.domain.product.dto.request.CreateProductRequest;
import com.sky.ecommerce.domain.product.dto.request.UpdateProductRequest;
import com.sky.ecommerce.domain.product.dto.response.ProductDetailResponse;
import com.sky.ecommerce.domain.product.dto.response.ProductListResponse;
import com.sky.ecommerce.domain.product.service.ProductService;
import com.sky.ecommerce.security.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product", description = "상품 API")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "판매 중인 상품 목록 (인증 불필요)")
    public ResponseEntity<ApiResponse<Page<ProductListResponse>>> getProducts(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProducts(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "상품 상세 조회", description = "판매 중인 상품 상세 (인증 불필요)")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProduct(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "상품 등록", description = "SELLER 이상 권한 필요")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> createProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateProductRequest request) {
        ProductDetailResponse response = productService.createProduct(userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "상품 수정", description = "본인 상품만 수정 가능")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> updateProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        ProductDetailResponse response = productService.updateProduct(userDetails.getUserId(), id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "상품 삭제", description = "본인 상품 숨김 처리 (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id) {
        productService.deleteProduct(userDetails.getUserId(), id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
