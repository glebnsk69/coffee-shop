package com.example.coffeeshop.repository;

import com.example.coffeeshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue();
    List<Product> findByDiscount(Integer discount);
    List<Product> findByActiveAndDiscount(Boolean active,Integer discount);
    Optional<Product> findByName(String name);
}