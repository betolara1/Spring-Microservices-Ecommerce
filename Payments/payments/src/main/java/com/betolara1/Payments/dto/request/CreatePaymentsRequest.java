package com.betolara1.Payments.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.betolara1.Payments.model.Payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePaymentsRequest {
    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    @NotNull(message = "Payment date is required")
    private LocalDateTime paymentDate;

    @NotNull(message = "Status is required")
    private Payment.Status status;

    @NotNull(message = "Amount is required")
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
}