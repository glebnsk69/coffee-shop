package com.example.coffeeshop.dto;

import com.example.coffeeshop.entity.OrderStatus;

public record OrderStatusOption(
        OrderStatus value,
        String displayName
) {
}