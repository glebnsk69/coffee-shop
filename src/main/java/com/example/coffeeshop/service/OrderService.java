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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import com.example.coffeeshop.dto.OrderStatusOption;

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
        String role = getCurrentUserRole();

        if (!"CASHIER".equals(role) && !"ADMIN".equals(role)) {
            throw new AccessDeniedException("Недостаточно прав для создания заказа");
        }

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
    private boolean isAnonymous() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return authentication == null
                || authentication instanceof AnonymousAuthenticationToken;
    }
    public List<Order> getAllOrders() {
        if (isAnonymous()) {
            return orderRepository.findAllByStatusIn(
                    List.of(
                            OrderStatus.OPEN,
                            OrderStatus.INPROGRESS,
                            OrderStatus.READY
                    )
            );
        }

        String role = getCurrentUserRole();

        if ("CASHIER".equals(role) || "ADMIN".equals(role)) {
            return orderRepository.findAll();
        }

        throw new AccessDeniedException("Недостаточно прав");
    }


    public Optional<Order> findOrderById(Long orderId) {
        if (isAnonymous()) {
            return orderRepository.findByIdAndStatusIn(
                    orderId,
                    List.of(
                            OrderStatus.OPEN,
                            OrderStatus.INPROGRESS,
                            OrderStatus.READY
                    )
            );
        }

        String role = getCurrentUserRole();

        if ("CASHIER".equals(role) || "ADMIN".equals(role)) {
            return orderRepository.findById(orderId);
        }

        throw new AccessDeniedException("Недостаточно прав");
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
    public void changeOrderStatus(Long orderId, OrderStatus newStatus) {
        String role = getCurrentUserRole();

        if (!"CASHIER".equals(role) && !"ADMIN".equals(role)) {
            throw new AccessDeniedException(
                    "Недостаточно прав для изменения статуса заказа"
            );
        }

        Order order = findOrderById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Заказ не найден"));

        if (newStatus == null) {
            throw new IllegalArgumentException("Статус не может быть null");
        }

        if (newStatus == OrderStatus.INVALID) {
            if ("CASHIER".equals(role)
                    && order.getStatus() != OrderStatus.OPEN) {
                throw new AccessDeniedException(
                        "Кассир может пометить как INVALID только открытый заказ"
                );
            }
            order.markAsInvalid();
            return;
        }


        if (order.getStatus() == OrderStatus.INVALID) {
            if (!"ADMIN".equals(role)) {
                throw new AccessDeniedException(
                        "Только администратор может восстановить заказ"
                );
            }

            order.restoreOrder(newStatus);
            return;
        }

        if ("ADMIN".equals(role)) {
            order.changeStatusByAdmin(newStatus);
            return;
        }
        switch (newStatus) {
            case INPROGRESS -> order.startOrder();

            case READY -> order.readyOrder();

            case COMPLETED -> order.completeOrder();

            case CANCELLED -> order.cancelOrder();

            default -> throw new IllegalArgumentException(
                    "Недопустимый переход статуса"
            );
        }
    }

    public List<OrderStatusOption> getAvailableStatuses(Long orderId) {
        String role = getCurrentUserRole();

        Order order = findOrderById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Заказ не найден"));

        OrderStatus currentStatus = order.getStatus();

        List<OrderStatus> availableStatuses = new ArrayList<>();

        if ("ADMIN".equals(role)) {
            if (currentStatus == OrderStatus.INVALID) {
                availableStatuses.addAll(List.of(
                        OrderStatus.OPEN,
                        OrderStatus.INPROGRESS,
                        OrderStatus.READY,
                        OrderStatus.COMPLETED,
                        OrderStatus.CANCELLED
                ));
            } else {
                availableStatuses.addAll(List.of(
                        OrderStatus.OPEN,
                        OrderStatus.INPROGRESS,
                        OrderStatus.READY,
                        OrderStatus.COMPLETED,
                        OrderStatus.CANCELLED
                ));
                availableStatuses.remove(currentStatus);
            }
        } else if ("CASHIER".equals(role)) {
            switch (currentStatus) {
                case OPEN -> availableStatuses.addAll(List.of(
                        OrderStatus.INPROGRESS,
                        OrderStatus.CANCELLED
                ));
                case INPROGRESS -> availableStatuses.add(OrderStatus.READY);
                case READY -> availableStatuses.add(OrderStatus.COMPLETED);
                default -> {
                }
            }
        }

        return availableStatuses.stream()
                .map(status -> new OrderStatusOption(
                        status,
                        status.getDisplayName()
                ))
                .toList();
    }


}
