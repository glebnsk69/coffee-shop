package com.example.coffeeshop.controller;

import com.example.coffeeshop.dto.*;
import com.example.coffeeshop.entity.Order;
import com.example.coffeeshop.entity.OrderItem;
import com.example.coffeeshop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "about - выводит название приложения и текущую версию",
            description = "входных данных нет. выводит текст с информацией о приложении"
    )
    @GetMapping("/api/about")
    public ResponseEntity<String> about(){
        ResponseEntity response = new ResponseEntity<>("<center>><H1>CoffeeShop<H1><br><H2>Version 0.1 </center>",HttpStatus.OK);
        return response;//"<H1>Hello WORLD!!!<H1>";
    }

    @Operation(
            summary = "Поиск по номеру заказа",
            description = "выводит заказ по номеру. Ошибку если заказ не найден или не активен"
    )
    @GetMapping("/api/orders/{id}")
    public ResponseEntity<?> findOrderById(@PathVariable Long id){
       Optional<Order> orderOptional = orderService.findOrderById(id).filter(order -> !order.isDeleted());
       if(orderOptional.isEmpty()) {
           //return ResponseEntity.notFound().build();
           return ResponseEntity
                   .status(HttpStatus.NOT_FOUND)
                   .body(new ApiError(404,"Not found","Заказ не найден"));
       }
        Order order = orderOptional.get();
        List<OrderItem> orderItems = orderService.getOrderItems(id);
        List<OrderItemResponse> items = orderItems.stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getName(),
                        item.getQuantity(),
                        item.getCost(),
                        item.getDiscount(),
                        "/api/products/"+item.getProduct().getId()+"/image"
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
                        item.getQuantity(),
                        item.getCost(),
                        item.getDiscount(),
                        "/api/products/" + item.getProduct().getId() + "/image"

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
    @DeleteMapping("/api/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
//Long productId, String productName, Integer quantity