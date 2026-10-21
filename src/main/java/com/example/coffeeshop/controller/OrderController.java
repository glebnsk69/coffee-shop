package com.example.coffeeshop.controller;

import com.example.coffeeshop.dto.OrderItemResponse;
import com.example.coffeeshop.dto.OrderResponse;
import com.example.coffeeshop.entity.Order;
import com.example.coffeeshop.entity.OrderItem;
import com.example.coffeeshop.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class OrderController {
    private final OrderService orderService;


    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @GetMapping("/api/orders")
    public List<Order> getActiveOrders(){
        return orderService.getAllOrders();
    }
    @GetMapping("/api/about")
    public String about(){
        return "<H1>Hello WORLD!!!<H1>";
    }

    @GetMapping("/api/orders/{id}")
    public ResponseEntity<OrderResponse> findOrderById(@PathVariable Long id){
       Optional<Order> orderOptional = orderService.findOrderById(id).filter(order -> !order.isDeleted());
       if(orderOptional.isEmpty()) {
           return ResponseEntity.notFound().build();
       }
        Order order = orderOptional.get();
        List<OrderItem> orderItems = orderService.getOrderItems(id);
        List<OrderItemResponse> items = orderItems.stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getName(),
                        item.getQuantity()
                ))
                .toList();
        OrderResponse response = new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getDurationSeconds(),
                items
        );

    return ResponseEntity.ok(response);
    }
}
//Long productId, String productName, Integer quantity