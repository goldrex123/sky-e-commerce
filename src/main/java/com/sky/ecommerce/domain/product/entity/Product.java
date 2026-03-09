package com.sky.ecommerce.domain.product.entity;

import com.sky.ecommerce.common.entity.BaseTimeEntity;
import com.sky.ecommerce.domain.user.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer basePrice;

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "product")
    private List<ProductOption> options = new ArrayList<>();

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    /**
     * 상품 생성 팩토리 메서드
     */
    public static Product create(User seller, Category category, String name,
                                 Integer basePrice, String description, ProductStatus status) {
        Product product = new Product();
        product.seller = seller;
        product.category = category;
        product.name = name;
        product.basePrice = basePrice;
        product.description = description;
        product.status = status;
        return product;
    }

    public void update(String name, Integer basePrice, String description, ProductStatus status, Category category) {
        this.name = name;
        this.basePrice = basePrice;
        this.description = description;
        this.status = status;
        this.category = category == null ? this.category : category;
    }


    /**
     * 상품 숨김 처리 (soft delete)
     * 실제 삭제 대신 status를 HIDDEN으로 변경해 주문 이력 참조를 보호합니다.
     */
    public void hide() {
        this.status = ProductStatus.HIDDEN;
    }
}
