package com.sky.ecommerce.domain.point.entity;

import com.sky.ecommerce.common.entity.BaseTimeEntity;
import com.sky.ecommerce.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory extends BaseTimeEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private Long amount;

    @Enumerated(EnumType.STRING)
    private PointSourceType sourceType;

    private Long referenceId;
}
