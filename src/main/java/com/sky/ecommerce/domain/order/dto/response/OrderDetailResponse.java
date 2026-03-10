package com.sky.ecommerce.domain.order.dto.response;

import com.sky.ecommerce.domain.order.entity.Order;
import com.sky.ecommerce.domain.order.entity.OrderStatus;
import com.sky.ecommerce.domain.order.entity.ShippingAddress;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
        Long orderId,
        Integer totalAmount,
        Integer discountAmount,
        Integer pointUsedAmount,
        Integer finalAmount,
        OrderStatus orderStatus,
        ShippingAddress shippingAddress,
        String orderUserEmail,
        List<OrderItemResponse> orderItems,
        LocalDateTime orderAt
) {

    public static OrderDetailResponse from(Order order) {
        return new OrderDetailResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getDiscountAmount(),
                order.getPointUsedAmount(),
                order.getFinalAmount(),
                order.getOrderStatus(),
                order.getShippingAddress(),
                order.getUser().getEmail(),
                order.getItems().stream().map(OrderItemResponse::from).toList(),
                order.getCreatedAt()
        );
    }
}
