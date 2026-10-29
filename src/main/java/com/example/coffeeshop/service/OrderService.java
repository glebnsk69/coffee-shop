package com.example.coffeeshop.service;

import com.example.coffeeshop.dto.CreateOrderRequest;
import com.example.coffeeshop.dto.OrderItemRequest;
import com.example.coffeeshop.entity.Order;
import com.example.coffeeshop.entity.OrderItem;
import com.example.coffeeshop.entity.OrderStatus;
import com.example.coffeeshop.entity.Product;
import com.example.coffeeshop.repository.OrderItemRepository;
import com.example.coffeeshop.repository.OrderRepository;
import com.example.coffeeshop.repository.ProductRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    private String getCurrentUserRole() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Пользователь не аутентифицирован");
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring(5))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("Роль пользователя не найдена"));
    }

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }
    @Transactional
    public Order createOrder(CreateOrderRequest request){
        if(request==null||request.getItems()==null
                || request.getItems().isEmpty()) throw new IllegalArgumentException("Order request must contain at least one item");

        Map<Long, Integer> quantityMap = new HashMap<>();
        for(OrderItemRequest item : request.getItems()){
            if (item.getQuantity()==null
                    || item.getQuantity()<=0
                    || item.getProductId()==null) throw new IllegalArgumentException(
                            "Product ID must not be null and quantity must be positive");
            quantityMap.put(
                    item.getProductId(),
                    Math.addExact(quantityMap.getOrDefault(item.getProductId(),0),item.getQuantity()));
        }
        Map<Long, Product> productsById = new HashMap<>();
        quantityMap.entrySet().forEach(entry->{
            Product product = productRepository.findById(entry.getKey()).orElseThrow(() -> new IllegalArgumentException("Product not found"));
            if(!Boolean.TRUE.equals(product.getActive()))
                throw new IllegalArgumentException("Order createOrder: Only active product can use in order");

            productsById.put(entry.getKey(),product);
        });

        Order order = orderRepository.save(new Order());
        productsById.forEach((aLong, product) -> {
            orderItemRepository.save(new OrderItem(product,order,quantityMap.get(aLong)));
        });


        return order;
    }
    public List<Order> getAllOrders(){
        return orderRepository.findAllByDeletedFalse();
    }
    public Optional<Order> findOrderById(Long orderId){

        return orderRepository.findByIdAndDeletedFalse(orderId);//orderRepository.findById(orderId);
    }
    public List<OrderItem> getOrderItems(Long orderId){
        return orderItemRepository.findByOrder_Id(orderId);
    }
    @Transactional
    public void startOrder(Long orderID){
        orderRepository.findById(orderID).ifPresent(order -> order.startOrder());
    }
    @Transactional
    public void completeOrder(Long orderId){
        orderRepository.findById(orderId).ifPresent(order -> order.completeOrder());
    }
    @Transactional
    public void cancelOrder(Long orderId){
        orderRepository.findById(orderId).ifPresent(order -> order.cancelOrder());
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        String role = getCurrentUserRole();

        if (!"ADMIN".equals(role)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Удалять заказы может только администратор");
        }

        Order order = findOrderById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Заказ не найден"));

        order.deleteOrder();
    }


    @Transactional
    public void changeOrderStatus(Long orderId, OrderStatus newStatus) {
        String role = getCurrentUserRole();

        if ("GUEST".equals(role)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Гостю запрещено изменять статус заказа");
        }

        Order order = findOrderById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Заказ не найден"));

        switch (newStatus) {
            case INPROGRESS -> order.startOrder();

            case COMPLETED -> order.completeOrder();

            case CANCELLED -> {
                if ("ADMIN".equals(role)
                        && order.getStatus() == OrderStatus.INPROGRESS) {
                    throw new IllegalStateException(
                            "Администратор не может отменить заказ в обработке");
                }
                order.cancelOrder();
            }

            default -> throw new IllegalArgumentException("Ошибка в статусе");
        }
    }

}
