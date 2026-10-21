package com.example.coffeeshop.controller;

import com.example.coffeeshop.entity.Product;
import com.example.coffeeshop.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class ProductController {
    private final ProductService  productService;


    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    @GetMapping("/api/products")
    public List<Product> getActiveProducts(){
        return productService.getActiveProducts();
    }

    @GetMapping("/api/products/{id}")
    public ResponseEntity<Product> findProductById(@PathVariable Long id){
        return productService.findProductById(id)
                .map(product -> ResponseEntity.ok(product))
                .orElseGet(() -> ResponseEntity.notFound().build()
                );
    }
}
