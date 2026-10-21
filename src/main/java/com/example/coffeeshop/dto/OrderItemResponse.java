package com.example.coffeeshop.dto;

public class OrderItemResponse {
    private Long productId;
    private String productName; // Полезно добавить для фронтенда
    private Integer quantity;

    public OrderItemResponse(Long productId, String productName, Integer quantity) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
    }

    // Геттеры
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Integer getQuantity() { return quantity; }
}
