package com.example.coffeeshop.controller;

import com.example.coffeeshop.entity.Product;
import com.example.coffeeshop.service.ProductService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.coffeeshop.dto.ApiError;

import java.io.IOException;
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
    public ResponseEntity<?> findProductById(@PathVariable Long id){
        return productService.findProductById(id)
                .<ResponseEntity<?>>map(product -> ResponseEntity.ok(product))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(
                                404,
                                "Not Found",
                                "Товар не найден"
                        ))
                );
    }

    @GetMapping("/api/products/{id}/image")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long id)
            throws IOException {

        byte[] image = productService.getProductImage(id);
        String contentType = productService.getProductImageContentType(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(image);
    }



    @PostMapping(
            value = "/api/products/{id}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Product> uploadProductImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        Product product = productService.uploadProductImage(
                id,
                file.getBytes()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }


}
