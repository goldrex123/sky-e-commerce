package com.sky.ecommerce.domain.product.dto.response;

import com.sky.ecommerce.domain.product.entity.Product;
import com.sky.ecommerce.domain.product.entity.ProductImage;
import com.sky.ecommerce.domain.product.entity.ProductStatus;

public record ProductListResponse(
        Long productId,
        String name,
        Integer basePrice,
        String categoryName,
        String mainImageUrl,
        ProductStatus status
) {
    public static ProductListResponse from(Product product) {
        // 메인 이미지는 애플리케이션 레벨에서 필터링 (fetch join에서 isMain 조건 불가)
        String mainImageUrl = product.getImages().stream()
                .filter(ProductImage::isMain)
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse(null);

        return new ProductListResponse(
                product.getId(),
                product.getName(),
                product.getBasePrice(),
                product.getCategory() != null ? product.getCategory().getName() : null,
                mainImageUrl,
                product.getStatus()
        );
    }
}
