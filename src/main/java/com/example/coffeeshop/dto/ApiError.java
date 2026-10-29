package com.example.coffeeshop.dto;

public record ApiError(
        int status,
        String error,
        String message
) {
}