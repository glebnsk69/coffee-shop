package com.example.coffeeshop.repository;

import com.example.coffeeshop.entity.Order;

import com.example.coffeeshop.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrderRepository  extends JpaRepository<Order, Long> {
    List<Order> findAllByStatusIn(Collection<OrderStatus> statuses);

    Optional<Order> findByIdAndStatusIn(
            Long id,
            Collection<OrderStatus> statuses
    );



}
