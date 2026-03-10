package com.sky.ecommerce.domain.order.service;

import com.sky.ecommerce.domain.order.dto.response.OrderListResponse;
import com.sky.ecommerce.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;


    public Page<OrderListResponse> getOrders(Pageable pageable) {

    }

}
