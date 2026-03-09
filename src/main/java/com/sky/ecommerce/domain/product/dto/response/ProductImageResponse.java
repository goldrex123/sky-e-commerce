package com.sky.ecommerce.domain.product.dto.response;

import com.sky.ecommerce.domain.product.entity.ProductImage;

public record ProductImageResponse(
        Long imageId,
        String imageUrl,
        Integer sortOrder,
        boolean isMain
) {
    public static ProductImageResponse from(ProductImage image) {
        return new ProductImageResponse(
                image.getId(),
                image.getImageUrl(),
                image.getSortOrder(),
                image.isMain()
        );
    }
}
