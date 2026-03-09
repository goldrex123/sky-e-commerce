package com.sky.ecommerce.domain.product.dto.request;

import com.sky.ecommerce.domain.product.entity.ProductStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateProductRequest(

        Long categoryId,

        @NotBlank(message = "상품명은 필수입니다")
        String name,

        @NotNull(message = "기본 가격은 필수입니다")
        @Min(value = 0, message = "가격은 0 이상이어야 합니다")
        Integer basePrice,

        String description,

        @NotNull(message = "상품 상태는 필수입니다")
        ProductStatus status
) {
}
