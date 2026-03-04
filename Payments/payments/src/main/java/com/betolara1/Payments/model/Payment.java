package com.betolara1.Payments.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long orderId;

    @Column(unique = true)
    private String transactionId;

    private LocalDateTime paymentDate;
    private Status status;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    private String paymentMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Status {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED
    }
}
