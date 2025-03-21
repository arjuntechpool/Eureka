package com.techpool.product_service.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.techpool.product_service.model.Product;

@RestController
@RequestMapping("/products/api")
public class ProductController {

    // Simple in-memory product list
    private List<Product> products = Arrays.asList(
        new Product(1L, "Laptop", "High-performance laptop", 1299.99),
        new Product(2L, "Smartphone", "Latest smartphone model", 899.99),
        new Product(3L, "Headphones", "Noise-cancelling headphones", 249.99)
    );

    @GetMapping("/products")
    @HystrixCommand(fallbackMethod = "getAllProductsFallback")
    public List<Product> getAllProducts() {
        // Simulate a potential failure scenario
        if (Math.random() > 0.9) {
            throw new RuntimeException("Service temporarily unavailable");
        }
        return products;
    }

    public List<Product> getAllProductsFallback() {
        return Collections.singletonList(new Product(0L, "Fallback Product", "Service is temporarily down", 0.0));
    }

    @GetMapping("/products/{id}")
    @HystrixCommand(fallbackMethod = "getProductByIdFallback")
    public Product getProductById(@PathVariable Long id) {
        // Simulate a potential failure scenario
        if (Math.random() > 0.9) {
            throw new RuntimeException("Service temporarily unavailable");
        }
        return products.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public Product getProductByIdFallback(Long id) {
        return new Product(id, "Fallback Product", "Service is temporarily down", 0.0);
    }
}