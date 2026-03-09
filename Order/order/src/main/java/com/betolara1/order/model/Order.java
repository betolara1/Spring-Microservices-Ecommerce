package com.betolara1.order.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalAmount;

    private String shippingAddress;
    private String sku;
    private Integer quantity;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Status {
        PENDING,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        PAID,
        APPROVED,
        FAILED,
        OUT_OF_STOCK,
        RESERVED,
        REFUND
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
