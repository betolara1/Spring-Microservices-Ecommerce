package com.betolara1.Payments.dto.response;

import java.time.LocalDateTime;

import com.betolara1.Payments.model.Payment;

import lombok.Data;

@Data
public class PaymentDTO {
    private Long id;
    private Long order_id;
    private String transaction_id;
    private LocalDateTime payment_date;
    private Status status;
    private double amount;
    private String payment_method;


    private enum Status {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED
    }

    public PaymentDTO(Payment payment) {
        this.id = payment.getId();
        this.order_id = payment.getOrderId();
        this.transaction_id = payment.getTransactionId();
        this.payment_date = payment.getPaymentDate();
        this.status = Status.valueOf(payment.getStatus().name());
        this.amount = payment.getAmount();
        this.payment_method = payment.getPaymentMethod();
    }
}
