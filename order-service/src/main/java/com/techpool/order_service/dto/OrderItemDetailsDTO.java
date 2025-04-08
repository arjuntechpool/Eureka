package com.techpool.order_service.dto;

import com.techpool.order_service.model.OrderItem;
import com.techpool.order_service.model.Product;

public class OrderItemDetailsDTO {
    private Long productId;
    private String productName;
    private String productDescription;
    private int quantity;
    private double price;
    
    // Constructor
    public OrderItemDetailsDTO(OrderItem orderItem, Product product) {
        this.productId = orderItem.getProductId();
        this.productName = product != null ? product.getName() : "Unknown Product";
        this.productDescription = product != null ? product.getDescription() : "Product details unavailable";
        this.quantity = orderItem.getQuantity();
        this.price = orderItem.getPrice();
    }
    
    // Getters and setters
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductDescription() {
        return productDescription;
    }
    
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
}