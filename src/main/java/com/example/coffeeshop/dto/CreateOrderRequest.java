package com.example.coffeeshop.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CreateOrderRequest {
    private List<OrderItemRequest> items;

    public CreateOrderRequest(List<OrderItemRequest> items) {
        this.items = items;
    }
    public List<OrderItemRequest> getItems(){
        return items;
    }

}
