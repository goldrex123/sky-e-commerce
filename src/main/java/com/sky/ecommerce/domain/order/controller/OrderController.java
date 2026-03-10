package com.sky.ecommerce.domain.order.controller;

import com.sky.ecommerce.common.ApiResponse;
import com.sky.ecommerce.domain.order.dto.response.OrderDetailResponse;
import com.sky.ecommerce.domain.order.dto.response.OrderListResponse;
import com.sky.ecommerce.domain.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order", description = "주문 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@PreAuthorize("hasRole('USER')")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "주문 목록 조회", description = "내가 주문한 주문 목록 조회 [USER 권한] ")
    public ResponseEntity<ApiResponse<Page<OrderListResponse>>> getOrders(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return null;
    }

    @GetMapping("/{id}")
    @Operation(summary = "주문 상세 조회", description = "주문 정보 상세 조회 [USER 권한]")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrder(
            @PathVariable Long id
    ) {
        return null;
    }
}
