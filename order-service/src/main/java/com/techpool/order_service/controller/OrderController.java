package com.techpool.order_service.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.techpool.order_service.client.ProductClient;
import com.techpool.order_service.dto.OrderDetailsDTO;
import com.techpool.order_service.dto.OrderItemDetailsDTO;
import com.techpool.order_service.model.Order;
import com.techpool.order_service.model.OrderItem;
import com.techpool.order_service.model.Product;

@RestController
@RequestMapping("/orders/api")
public class OrderController {

    @Autowired
    private ProductClient productClient;

    // Simple in-memory order list
    private List<Order> orders = Arrays.asList(
            new Order(1L, 101L, LocalDateTime.now().minusDays(1), "COMPLETED",
                    Arrays.asList(new OrderItem(1L, 1, 1299.99), new OrderItem(3L, 1, 249.99))),
            new Order(2L, 102L, LocalDateTime.now().minusHours(5), "PROCESSING",
                    Arrays.asList(new OrderItem(2L, 2, 899.99)))
    );

    @GetMapping("/orders")
    @HystrixCommand(fallbackMethod = "getAllOrdersFallback")
    public List<Order> getAllOrders() {
        // Simulate a potential failure scenario
        if (Math.random() > 0.9) {
            throw new RuntimeException("Service temporarily unavailable");
        }
        return orders;
    }

    public List<Order> getAllOrdersFallback() {
        return Collections.singletonList(
                new Order(0L, 0L, LocalDateTime.now(), "FALLBACK",
                        Collections.singletonList(new OrderItem(0L, 0, 0.0))));
    }

    @GetMapping("/orders/{id}")
    @HystrixCommand(fallbackMethod = "getOrderByIdFallback")
    public Order getOrderById(@PathVariable Long id) {
        // Simulate a potential failure scenario
        if (Math.random() > 0.9) {
            throw new RuntimeException("Service temporarily unavailable");
        }
        return orders.stream()
                .filter(order -> order.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public Order getOrderByIdFallback(Long id) {
        return new Order(id, 0L, LocalDateTime.now(), "FALLBACK",
                Collections.singletonList(new OrderItem(0L, 0, 0.0)));
    }

    // New endpoint that integrates with product service
    @GetMapping("/orders-with-products")
    @HystrixCommand(fallbackMethod = "getOrdersWithProductsFallback")
    public List<OrderDetailsDTO> getOrdersWithProducts() {
        return orders.stream()
                .map(order -> {
                    List<OrderItemDetailsDTO> itemsWithDetails = new ArrayList<>();

                    for (OrderItem item : order.getItems()) {
                        try {
                            // Call product service via Feign
                            Product product = productClient.getProductById(item.getProductId());
                            System.out.println("Successfully retrieved product: " + product.getName() + " for ID: " + item.getProductId());
                            itemsWithDetails.add(new OrderItemDetailsDTO(item, product));
                        } catch (Exception e) {
                            // Fallback in case product service is down
                            System.err.println("Error calling product service: " + e.getMessage());
                            e.printStackTrace();
                            itemsWithDetails.add(new OrderItemDetailsDTO(item, null));
                        }
                    }

                    return new OrderDetailsDTO(order, itemsWithDetails);
                })
                .collect(Collectors.toList());
    }

    public List<OrderDetailsDTO> getOrdersWithProductsFallback() {
        Order fallbackOrder = new Order(0L, 0L, LocalDateTime.now(), "FALLBACK",
                Collections.singletonList(new OrderItem(0L, 0, 0.0)));

        OrderItemDetailsDTO fallbackItem = new OrderItemDetailsDTO(
                new OrderItem(0L, 0, 0.0),
                new Product(0L, "Fallback Product", "Service temporarily unavailable", 0.0)
        );

        return Collections.singletonList(
                new OrderDetailsDTO(fallbackOrder, Collections.singletonList(fallbackItem))
        );
    }

    // New endpoint to get a specific order with product details
    @GetMapping("/orders-with-products/{id}")
    @HystrixCommand(fallbackMethod = "getOrderWithProductsByIdFallback")
    public OrderDetailsDTO getOrderWithProductsById(@PathVariable Long id) {
        Order order = orders.stream()
                .filter(o -> o.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        List<OrderItemDetailsDTO> itemsWithDetails = new ArrayList<>();

        for (OrderItem item : order.getItems()) {
            try {
                // Call product service via Feign
                Product product = productClient.getProductById(item.getProductId());
                itemsWithDetails.add(new OrderItemDetailsDTO(item, product));
            } catch (Exception e) {
                // Fallback in case product service is down
                itemsWithDetails.add(new OrderItemDetailsDTO(item, null));
            }
        }

        return new OrderDetailsDTO(order, itemsWithDetails);
    }

    public OrderDetailsDTO getOrderWithProductsByIdFallback(Long id) {
        Order fallbackOrder = new Order(id, 0L, LocalDateTime.now(), "FALLBACK",
                Collections.singletonList(new OrderItem(0L, 0, 0.0)));

        OrderItemDetailsDTO fallbackItem = new OrderItemDetailsDTO(
                new OrderItem(0L, 0, 0.0),
                new Product(0L, "Fallback Product", "Service temporarily unavailable", 0.0)
        );

        return new OrderDetailsDTO(fallbackOrder, Collections.singletonList(fallbackItem));
    }

    // Test endpoint to directly verify product service connectivity
    @GetMapping("/test-product-service/{id}")
    public Product testProductService(@PathVariable Long id) {
        try {
            return productClient.getProductById(id);
        } catch (Exception e) {
            System.err.println("Error in test endpoint: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
