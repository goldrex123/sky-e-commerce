package com.sky.ecommerce.domain.product.dto.response;

import com.sky.ecommerce.domain.product.entity.Product;
import lombok.Builder;

@Builder
public record ProductResponse(
        Long productId,
        String productName
) {


    public static ProductResponse ofResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .build();
    }
}
