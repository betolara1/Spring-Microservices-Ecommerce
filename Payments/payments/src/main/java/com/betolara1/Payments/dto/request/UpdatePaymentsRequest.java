package com.betolara1.Payments.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.betolara1.Payments.model.Payment;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

@Data
public class UpdatePaymentsRequest {
    private Long orderId;

    private String transactionId;

    private LocalDateTime paymentDate;

    private Payment.Status status;

    @DecimalMin("0.01")
    private BigDecimal amount;

    private String paymentMethod;
}