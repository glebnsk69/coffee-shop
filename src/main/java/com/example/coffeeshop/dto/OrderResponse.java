package com.example.coffeeshop.dto;

import com.example.coffeeshop.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        OrderStatus status,
        LocalDateTime createdAt,
        Long durationSeconds,
        List<OrderItemResponse> items
) {
}