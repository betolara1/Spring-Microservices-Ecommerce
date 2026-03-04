package com.betolara1.Payments.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.betolara1.Payments.model.Payment;

import lombok.Data;

@Data
public class PaymentDTO {
    private Long id;
    private Long orderId;
    private String transactionId;
    private LocalDateTime paymentDate;
    private Payment.Status status;
    private BigDecimal amount;
    private String paymentMethod;

    public PaymentDTO(){}

    public PaymentDTO(Payment payment) {
        this.id = payment.getId();
        this.orderId = payment.getOrderId();
        this.transactionId = payment.getTransactionId();
        this.paymentDate = payment.getPaymentDate();
        this.status = Payment.Status.valueOf(payment.getStatus().name());
        this.amount = payment.getAmount();
        this.paymentMethod = payment.getPaymentMethod();
    }
}
