package com.sky.ecommerce.domain.order.dto.response;

import com.sky.ecommerce.domain.order.entity.OrderItem;

public record OrderItemResponse(
        Long orderItemId,
        Long productId,
        String productName,
        String optionName,
        Integer price,
        Integer quantity
) {

    public static OrderItemResponse from(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getId(),
                orderItem.getProductId(),
                orderItem.getProductName(),
                orderItem.getOptionName(),
                orderItem.getPrice(),
                orderItem.getQuantity()
        );
    }
}
