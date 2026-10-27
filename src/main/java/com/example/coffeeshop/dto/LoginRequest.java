package com.example.coffeeshop.dto;

public record LoginRequest(
        String username,
        String password
) {
}