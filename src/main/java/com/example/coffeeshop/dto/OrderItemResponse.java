package com.example.coffeeshop.dto;

public class OrderItemResponse {
    private Long productId;

    private String productName; // Полезно добавить для фронтенда

    private Integer quantity;

    private Long cost;
    private Integer discount;
    private String imageUrl;
    public OrderItemResponse(Long productId, String productName, Integer quantity,Long cost,Integer discount,String imageUrl) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.cost=cost;
        this.discount = discount;
        this.imageUrl=imageUrl;
    }

    // Геттеры

    public String getImageUrl() {
        return imageUrl;
    }
    public Integer getDiscount() {
        return discount;
    }
    public Long getCost() {
        return cost;
    }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Integer getQuantity() { return quantity; }

}
