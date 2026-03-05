package com.sky.ecommerce.domain.order.entity;

import com.sky.ecommerce.common.entity.BaseTimeEntity;
import com.sky.ecommerce.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer totalAmount;

    private Integer discountAmount;

    private Integer pointUsedAmount;

    private Integer finalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Embedded
    private ShippingAddress shippingAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> items = new ArrayList<>();
}
