package com.sky.ecommerce.domain.product.dto.response;

import com.sky.ecommerce.domain.product.entity.ProductOption;

public record ProductOptionResponse(
        Long optionId,
        String optionName,
        Integer additionalPrice,
        Integer stock
) {
    public static ProductOptionResponse from(ProductOption option) {
        return new ProductOptionResponse(
                option.getId(),
                option.getOptionName(),
                option.getAdditionalPrice(),
                option.getStock()
        );
    }
}
