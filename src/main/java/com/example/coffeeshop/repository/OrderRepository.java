package com.example.coffeeshop.repository;

import com.example.coffeeshop.entity.Order;

import com.example.coffeeshop.entity.OrderItem;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository  extends JpaRepository<Order, Long> {
    List<Order> findAllByDeletedFalse();
    List<Order> findAllByDeletedTrue();

}
