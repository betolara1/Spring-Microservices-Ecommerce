package com.betolara1.payments.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.betolara1.payments.dto.request.CreatePaymentsRequest;
import com.betolara1.payments.dto.request.UpdatePaymentsRequest;
import com.betolara1.payments.dto.response.PaymentDTO;
import com.betolara1.payments.exception.NotFoundException;
import com.betolara1.payments.model.Payment;
import com.betolara1.payments.repository.PaymentRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void testGetAllPayments_Success() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(Payment.Status.COMPLETED);

        Page<Payment> page = new PageImpl<>(Collections.singletonList(payment));
        when(paymentRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<PaymentDTO> result = paymentService.getAllPayments(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetAllPayments_Empty() {
        Page<Payment> page = new PageImpl<>(Collections.emptyList());
        when(paymentRepository.findAll(any(PageRequest.class))).thenReturn(page);

        assertThrows(NotFoundException.class, () -> paymentService.getAllPayments(0, 10));
    }

    @Test
    void testGetPaymentByStatus_Success() {
        Payment payment = new Payment();
        payment.setStatus(Payment.Status.COMPLETED);

        Page<Payment> page = new PageImpl<>(Collections.singletonList(payment));
        when(paymentRepository.findByStatus(any(PageRequest.class), eq(Payment.Status.COMPLETED))).thenReturn(page);

        Page<PaymentDTO> result = paymentService.getPaymentByStatus(0, 10, Payment.Status.COMPLETED);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(Payment.Status.COMPLETED, result.getContent().get(0).getStatus());
    }

    @Test
    void testGetPaymentByPaymentMethod_Success() {
        Payment payment = new Payment();
        payment.setPaymentMethod("CREDIT_CARD");
        payment.setStatus(Payment.Status.COMPLETED);

        Page<Payment> page = new PageImpl<>(Collections.singletonList(payment));
        when(paymentRepository.findByPaymentMethod(any(PageRequest.class), eq("CREDIT_CARD"))).thenReturn(page);

        Page<PaymentDTO> result = paymentService.getPaymentByPaymentMethod(0, 10, "CREDIT_CARD");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("CREDIT_CARD", result.getContent().get(0).getPaymentMethod());
    }

    @Test
    void testGetPaymentById_Success() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(Payment.Status.COMPLETED);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        PaymentDTO result = paymentService.getPaymentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetPaymentById_NotFound() {
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> paymentService.getPaymentById(1L));
    }

    @Test
    void testGetPaymentByOrderId_Success() {
        Payment payment = new Payment();
        payment.setOrderId(1L);
        payment.setStatus(Payment.Status.COMPLETED);

        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(payment));

        PaymentDTO result = paymentService.getPaymentByOrderId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
    }

    @Test
    void testGetPaymentByTransactionId_Success() {
        Payment payment = new Payment();
        payment.setTransactionId("TXN123");
        payment.setStatus(Payment.Status.COMPLETED);

        when(paymentRepository.findByTransactionId("TXN123")).thenReturn(Optional.of(payment));

        PaymentDTO result = paymentService.getPaymentByTransactionId("TXN123");

        assertNotNull(result);
        assertEquals("TXN123", result.getTransactionId());
    }

    @Test
    void testSavePayment_Success() {
        CreatePaymentsRequest request = new CreatePaymentsRequest();
        request.setOrderId(1L);
        request.setStatus(Payment.Status.PENDING);

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setOrderId(1L);
        payment.setStatus(Payment.Status.PENDING);

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment result = paymentService.savePayment(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getOrderId());
    }

    @Test
    void testUpdatePayment_Success() {
        UpdatePaymentsRequest request = new UpdatePaymentsRequest();
        request.setOrderId(2L);
        request.setStatus(Payment.Status.COMPLETED);

        Payment existingPayment = new Payment();
        existingPayment.setId(1L);
        existingPayment.setOrderId(1L);
        existingPayment.setStatus(Payment.Status.PENDING);

        Payment updatedPayment = new Payment();
        updatedPayment.setId(1L);
        updatedPayment.setOrderId(2L);
        updatedPayment.setStatus(Payment.Status.COMPLETED);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(existingPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);

        Payment result = paymentService.updatePayment(1L, request);

        assertNotNull(result);
        assertEquals(2L, result.getOrderId());
        assertEquals(Payment.Status.COMPLETED, result.getStatus());
    }

    @Test
    void testUpdateStatus_Success() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setOrderId(1L);
        payment.setStatus(Payment.Status.PENDING);

        Payment updatedPayment = new Payment();
        updatedPayment.setId(1L);
        updatedPayment.setOrderId(1L);
        updatedPayment.setStatus(Payment.Status.COMPLETED);

        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(updatedPayment);

        Payment result = paymentService.updateStatus(1L, Payment.Status.COMPLETED);

        assertNotNull(result);
        assertEquals(Payment.Status.COMPLETED, result.getStatus());
    }

    @Test
    void testDeletePayment_Success() {
        Payment payment = new Payment();
        payment.setId(1L);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        doNothing().when(paymentRepository).delete(payment);

        paymentService.deletePayment(1L);

        verify(paymentRepository, times(1)).delete(payment);
    }
}
