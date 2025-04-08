package com.techpool.order_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.techpool.order_service.model.Product;

@FeignClient(name = "product-service", url = "http://localhost:8081")
public interface ProductClient {
    
    @GetMapping("/products/api/products")
    List<Product> getAllProducts();
    
    @GetMapping("/products/api/products/{id}")
    Product getProductById(@PathVariable("id") Long id);
}