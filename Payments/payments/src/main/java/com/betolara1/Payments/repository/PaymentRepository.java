package com.betolara1.Payments.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.betolara1.Payments.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(Long orderId);
    Optional<Payment> findByTransactionId(String transactionId);
    Page<Payment> findByStatus(Pageable pageable, Payment.Status status);
    Page<Payment> findByPaymentMethod(Pageable pageable, String paymentMethod);
}
