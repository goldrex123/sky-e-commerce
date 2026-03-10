package com.sky.ecommerce.domain.order.dto.response;

import com.sky.ecommerce.domain.order.entity.Order;
import com.sky.ecommerce.domain.order.entity.OrderStatus;

public record OrderListResponse(
        Long orderId,
        Integer finalAmount,
        OrderStatus orderStatus,
        String orderUserEmail,
        OrderItemResponse firstOrderItem,
        Integer orderItemCount
) {

    public static OrderListResponse from(Order order) {
        String email = order.getUser().getEmail();

        return new OrderListResponse(
                order.getId(),
                order.getFinalAmount(),
                order.getOrderStatus(),
                email,
                OrderItemResponse.from(order.getItems().getFirst()),
                order.getItems().size()
        );
    }
}
