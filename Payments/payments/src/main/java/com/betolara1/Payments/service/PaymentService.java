package com.betolara1.Payments.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.betolara1.Payments.dto.request.CreatePaymentsRequest;
import com.betolara1.Payments.dto.request.UpdatePaymentsRequest;
import com.betolara1.Payments.dto.response.PaymentDTO;
import com.betolara1.Payments.exception.NotFoundException;
import com.betolara1.Payments.model.Payment;
import com.betolara1.Payments.repository.PaymentRepository;

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

    public Payment savePayment(CreatePaymentsRequest request) {
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setTransactionId(request.getTransactionId());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setStatus(Payment.Status.valueOf(request.getStatus().name()));
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        return paymentRepository.save(payment);
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

    public PaymentDTO getPaymentByStatus(Payment.Status status) {
        Payment payment = paymentRepository.findByStatus(status).orElseThrow(() -> new NotFoundException("Pagamento não encontrado com Status: " + status));
        return new PaymentDTO(payment);
    }

    public PaymentDTO getPaymentByPaymentMethod(String paymentMethod) {
        Payment payment = paymentRepository.findByPaymentMethod(paymentMethod).orElseThrow(() -> new NotFoundException("Pagamento não encontrado com Método de Pagamento: " + paymentMethod));
        return new PaymentDTO(payment);
    }

    public Payment updatePayment(Long id, UpdatePaymentsRequest updatedPayment) {
        Payment existingPayment = paymentRepository.findById(id).orElseThrow(() -> new NotFoundException("Pagamento não encontrado com ID: " + id));

        existingPayment.setOrderId(updatedPayment.getOrderId());
        existingPayment.setTransactionId(updatedPayment.getTransactionId());
        existingPayment.setPaymentDate(updatedPayment.getPaymentDate());
        existingPayment.setStatus(Payment.Status.valueOf(updatedPayment.getStatus().name()));
        existingPayment.setAmount(updatedPayment.getAmount());
        existingPayment.setPaymentMethod(updatedPayment.getPaymentMethod());

        return paymentRepository.save(existingPayment);
    }

    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new NotFoundException("Pagamento não encontrado com ID: " + id));
        paymentRepository.delete(payment);
    }
}
