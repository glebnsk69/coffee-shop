package com.example.coffeeshop.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}