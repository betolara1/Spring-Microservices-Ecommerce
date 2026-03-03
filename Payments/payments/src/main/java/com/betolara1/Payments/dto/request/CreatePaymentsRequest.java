package com.betolara1.Payments.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePaymentsRequest {
    @NotBlank(message = "Order ID is required")
    private Long orderId;

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    @NotBlank(message = "Payment date is required")
    private LocalDateTime paymentDate;

    @NotBlank(message = "Status is required")
    private Status status;

    @NotBlank(message = "Amount is required")
    private double amount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    public enum Status {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED
    }
}