package com.techpool.order_service.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.techpool.order_service.model.Order;

public class OrderDetailsDTO {
    private Long id;
    private Long customerId;
    private LocalDateTime orderDate;
    private String status;
    private List<OrderItemDetailsDTO> items;
    
    // Constructor to create from an Order
    public OrderDetailsDTO(Order order, List<OrderItemDetailsDTO> items) {
        this.id = order.getId();
        this.customerId = order.getCustomerId();
        this.orderDate = order.getOrderDate();
        this.status = order.getStatus();
        this.items = items;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<OrderItemDetailsDTO> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemDetailsDTO> items) {
        this.items = items;
    }
}