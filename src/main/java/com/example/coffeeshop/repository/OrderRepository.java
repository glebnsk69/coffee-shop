package com.example.coffeeshop.repository;

import com.example.coffeeshop.entity.Order;
import com.example.coffeeshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository  extends JpaRepository<Order, Long> {

}
