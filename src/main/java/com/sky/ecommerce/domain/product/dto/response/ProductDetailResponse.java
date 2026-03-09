package com.sky.ecommerce.domain.product.dto.response;

import com.sky.ecommerce.domain.product.entity.Product;
import com.sky.ecommerce.domain.product.entity.ProductStatus;

import java.util.List;

public record ProductDetailResponse(
        Long productId,
        String name,
        Integer basePrice,
        String description,
        String categoryName,
        ProductStatus status,
        List<ProductOptionResponse> options,
        List<ProductImageResponse> images
) {
    public static ProductDetailResponse from(Product product) {
        List<ProductOptionResponse> options = product.getOptions().stream()
                .map(ProductOptionResponse::from)
                .toList();

        List<ProductImageResponse> images = product.getImages().stream()
                .map(ProductImageResponse::from)
                .toList();

        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getBasePrice(),
                product.getDescription(),
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getStatus(),
                options,
                images
        );
    }
}
