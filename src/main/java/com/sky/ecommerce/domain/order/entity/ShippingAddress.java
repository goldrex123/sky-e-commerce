package com.sky.ecommerce.domain.order.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class ShippingAddress {

    private String zipCode;
    private String basicAddress;
    private String detailAddress;

    private String receiverName;
    private String phoneNumber;

    @Builder
    public ShippingAddress(String zipCode, String basicAddress, String detailAddress, String receiverName, String phoneNumber) {
        this.zipCode = zipCode;
        this.basicAddress = basicAddress;
        this.detailAddress = detailAddress;
        this.receiverName = receiverName;
        this.phoneNumber = phoneNumber;
    }
}

