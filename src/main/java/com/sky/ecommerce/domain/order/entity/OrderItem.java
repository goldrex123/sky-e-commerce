package com.sky.ecommerce.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private String productName;
    private String optionName;
    private Integer price;
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;
}
