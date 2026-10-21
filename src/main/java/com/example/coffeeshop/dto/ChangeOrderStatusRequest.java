package com.example.coffeeshop.dto;

import com.example.coffeeshop.entity.OrderStatus;

public record ChangeOrderStatusRequest (
        OrderStatus status
)
        {
}
