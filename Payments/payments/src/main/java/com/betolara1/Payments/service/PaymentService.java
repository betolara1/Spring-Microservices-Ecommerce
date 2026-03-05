package com.betolara1.payments.service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.betolara1.payments.dto.request.CreatePaymentsRequest;
import com.betolara1.payments.dto.request.UpdatePaymentsRequest;
import com.betolara1.payments.dto.response.PaymentDTO;
import com.betolara1.payments.exception.NotFoundException;
import com.betolara1.payments.model.Payment;
import com.betolara1.payments.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Page<PaymentDTO> getAllPayments(int page, int size) {
        Page<Payment> payments = paymentRepository.findAll(PageRequest.of(page, size));

        if (payments.isEmpty()) {
            throw new NotFoundException("Nenhum pagamento registrado.");
        }
        return payments.map(PaymentDTO::new);
    }

    public Page<PaymentDTO> getPaymentByStatus(int page, int size, Payment.Status status) {
        Page<Payment> payments = paymentRepository.findByStatus(PageRequest.of(page, size), status);
        if (payments.isEmpty()) {
            throw new NotFoundException("Nenhum pagamento registrado.");
        }
        return payments.map(PaymentDTO::new);
    }

    public Page<PaymentDTO> getPaymentByPaymentMethod(int page, int size, String paymentMethod) {
        Page<Payment> payments = paymentRepository.findByPaymentMethod(PageRequest.of(page, size), paymentMethod);
        if (payments.isEmpty()) {
            throw new NotFoundException("Nenhum pagamento registrado.");
        }
        return payments.map(PaymentDTO::new);
    }

    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new NotFoundException("Pagamento não encontrado com ID: " + id));
        return new PaymentDTO(payment);
    }

    public PaymentDTO getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new NotFoundException("Pagamento não encontrado com ID: " + orderId));
        return new PaymentDTO(payment);
    }

    public PaymentDTO getPaymentByTransactionId(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId).orElseThrow(() -> new NotFoundException("Pagamento não encontrado com ID: " + transactionId));
        return new PaymentDTO(payment);
    }

    public Payment updatePayment(Long id, UpdatePaymentsRequest updatedPayment) {
        Payment existingPayment = paymentRepository.findById(id).orElseThrow(() -> new NotFoundException("Pagamento não encontrado com ID: " + id));

        if (updatedPayment.getOrderId() != null) {
            existingPayment.setOrderId(updatedPayment.getOrderId());
        }
        if (updatedPayment.getStatus() != null) {
            existingPayment.setStatus(updatedPayment.getStatus()); // Sem usar o .name()
        }
        if (updatedPayment.getPaymentDate() != null) {
            existingPayment.setPaymentDate(updatedPayment.getPaymentDate());
        }
        if (updatedPayment.getAmount() != null) {
            existingPayment.setAmount(updatedPayment.getAmount());
        }
        if (updatedPayment.getPaymentMethod() != null) {
            existingPayment.setPaymentMethod(updatedPayment.getPaymentMethod());
        }

        return paymentRepository.save(existingPayment);
    }

    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new NotFoundException("Pagamento não encontrado com ID: " + id));
        paymentRepository.delete(payment);
    }

    public Payment savePayment(CreatePaymentsRequest request) {

        // CONDIÇÃO APENAS PARA TESTAR O PAGAMENTO RECUSADO
        if (request.getAmount().compareTo(new BigDecimal("1000")) > 0) {
            Payment payment = new Payment();
            payment.setOrderId(request.getOrderId());
            payment.setAmount(request.getAmount());
            payment.setStatus(Payment.Status.FAILED);
            payment.setTransactionId(UUID.randomUUID().toString());
            payment.setPaymentDate(LocalDateTime.now());
            payment.setCreatedAt(LocalDateTime.now());
            payment.setPaymentMethod(request.getPaymentMethod());
            return paymentRepository.save(payment);
        }

        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setTransactionId(request.getTransactionId());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setStatus(Payment.Status.valueOf(request.getStatus().name()));
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        return paymentRepository.save(payment);
    }
}
