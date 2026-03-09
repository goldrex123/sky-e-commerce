package com.sky.ecommerce.domain.product.repository;

import com.sky.ecommerce.domain.product.entity.Product;
import com.sky.ecommerce.domain.product.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 목록 조회: status 필터 + category fetch join (N+1 방지)
     * images는 fetch join 대신 애플리케이션 레벨에서 메인 이미지 필터링
     */
    @Query(value = "SELECT p FROM Product p JOIN FETCH p.category WHERE p.status = :status",
    countQuery = "SELECT COUNT(*) from Product ")
    Page<Product> findAllByStatusWithCategory(@Param("status") ProductStatus status, Pageable pageable);

    /**
     * 상세 조회: category만 fetch join
     * options, images는 @BatchSize(size=100)으로 배치 로딩 (MultipleBagFetchException 방지)
     */
    @Query("""
            SELECT p FROM Product p
            JOIN FETCH p.category
            WHERE p.id = :id AND p.status = :status
            """)
    Optional<Product> findByIdAndStatusWithDetails(@Param("id") Long id, @Param("status") ProductStatus status);

    /**
     * 수정/삭제용: 판매자 소유권 확인 포함
     */
    Optional<Product> findByIdAndSellerId(Long id, Long sellerId);
}
