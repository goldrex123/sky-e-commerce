package com.sky.ecommerce.domain.product.service;

import com.sky.ecommerce.common.exception.BusinessException;
import com.sky.ecommerce.common.exception.ErrorCode;
import com.sky.ecommerce.domain.product.dto.request.CreateProductRequest;
import com.sky.ecommerce.domain.product.dto.request.UpdateProductRequest;
import com.sky.ecommerce.domain.product.dto.response.ProductDetailResponse;
import com.sky.ecommerce.domain.product.dto.response.ProductListResponse;
import com.sky.ecommerce.domain.product.entity.Category;
import com.sky.ecommerce.domain.product.entity.Product;
import com.sky.ecommerce.domain.product.entity.ProductStatus;
import com.sky.ecommerce.domain.product.repository.CategoryRepository;
import com.sky.ecommerce.domain.product.repository.ProductRepository;
import com.sky.ecommerce.domain.user.entity.User;
import com.sky.ecommerce.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    /**
     * 상품 목록 조회 (SALE 상태만, 페이징)
     */
    public Page<ProductListResponse> getProducts(Pageable pageable) {
        return productRepository.findAllByStatusWithCategory(ProductStatus.SALE, pageable)
                .map(ProductListResponse::from);
    }

    /**
     * 상품 상세 조회 (SALE 상태만)
     */
    public ProductDetailResponse getProduct(Long productId) {
        Product product = productRepository.findByIdAndStatusWithDetails(productId, ProductStatus.SALE)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        return ProductDetailResponse.from(product);
    }

    /**
     * 상품 등록 (SELLER 이상)
     */
    @Transactional
    public ProductDetailResponse createProduct(Long sellerId, CreateProductRequest request) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        Product product = Product.create(
                seller,
                category,
                request.name(),
                request.basePrice(),
                request.description(),
                request.status()
        );

        return ProductDetailResponse.from(productRepository.save(product));
    }

    /**
     * 상품 수정 (본인 상품만)
     */
    @Transactional
    public ProductDetailResponse updateProduct(Long sellerId, Long productId, UpdateProductRequest request) {
        Product product = productRepository.findByIdAndSellerId(productId, sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_FORBIDDEN));

        Category category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        }

        product.update(request.name(), request.basePrice(), request.description(), request.status(), category);

        return ProductDetailResponse.from(product);
    }

    /**
     * 상품 숨김 처리 (soft delete, 본인 상품만)
     */
    @Transactional
    public void deleteProduct(Long sellerId, Long productId) {
        Product product = productRepository.findByIdAndSellerId(productId, sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_FORBIDDEN));

        product.hide();
    }
}
