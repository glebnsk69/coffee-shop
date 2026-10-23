package com.example.coffeeshop.controller;

import com.example.coffeeshop.dto.ChangeOrderStatusRequest;
import com.example.coffeeshop.dto.CreateOrderRequest;
import com.example.coffeeshop.dto.OrderItemResponse;
import com.example.coffeeshop.dto.OrderResponse;
import com.example.coffeeshop.entity.Order;
import com.example.coffeeshop.entity.OrderItem;
import com.example.coffeeshop.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> about(){
        ResponseEntity response = new ResponseEntity<>("<H1>Hello WORLD!!!<H1>",HttpStatus.OK);
        return response;//"<H1>Hello WORLD!!!<H1>";
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

    @PostMapping("/api/orders")
    public ResponseEntity<OrderResponse>  createOrder(@RequestBody CreateOrderRequest request){
        Order order = orderService.createOrder(request);

        List<OrderItemResponse> items = orderService.getOrderItems(order.getId()).stream()
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

        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/api/orders/{id}/status")
    public ResponseEntity<Void> changeOrderStatus(
            @PathVariable Long id,
            @RequestBody ChangeOrderStatusRequest request) {
        orderService.changeOrderStatus(id,request.status());
        return ResponseEntity.ok().build();
    }
}
//Long productId, String productName, Integer quantity