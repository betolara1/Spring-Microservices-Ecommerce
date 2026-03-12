package com.betolara1.payments.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.betolara1.payments.dto.request.CreatePaymentsRequest;
import com.betolara1.payments.dto.request.UpdatePaymentsRequest;
import com.betolara1.payments.dto.response.PaymentDTO;
import com.betolara1.payments.exception.NotFoundException;
import com.betolara1.payments.model.Payment;
import com.betolara1.payments.service.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    @Test
    void testGetAllPayments_Success() {
        PaymentDTO payment = new PaymentDTO();
        payment.setId(1L);
        payment.setOrderId(1L);
        payment.setTransactionId("TXN123");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(Payment.Status.COMPLETED);
        payment.setAmount(BigDecimal.valueOf(100.0));
        payment.setPaymentMethod("CREDIT_CARD");

        Page<PaymentDTO> page = new PageImpl<>(Collections.singletonList(payment));

        when(paymentService.getAllPayments(0, 10)).thenReturn(page);

        ResponseEntity<Page<PaymentDTO>> response = paymentController.getAllPayments(0, 10, "ADMIN", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Page<PaymentDTO> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.getTotalElements());
        assertEquals(1, body.getContent().size());
    }

    @Test
    void testGetAllPayments_Empty() {
        when(paymentService.getAllPayments(0, 10)).thenThrow(new NotFoundException("Nenhum pagamento cadastrado."));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            paymentController.getAllPayments(0, 10, "ADMIN", 1L);
        });

        assertTrue(exception.getMessage().contains("Nenhum pagamento cadastrado."));
    }

    @Test
    void testGetPaymentByStatus() {
        PaymentDTO payment = new PaymentDTO();
        payment.setStatus(Payment.Status.COMPLETED);
        Page<PaymentDTO> page = new PageImpl<>(Collections.singletonList(payment));

        when(paymentService.getPaymentByStatus(0, 10, Payment.Status.COMPLETED)).thenReturn(page);

        ResponseEntity<Page<PaymentDTO>> response = paymentController.getPaymentByStatus(Payment.Status.COMPLETED, 0, 10, "ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Page<PaymentDTO> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.getTotalElements());
        assertEquals(1, body.getContent().size());
    }

    @Test
    void testGetPaymentByPaymentMethod() {
        PaymentDTO payment = new PaymentDTO();
        payment.setPaymentMethod("CREDIT_CARD");
        Page<PaymentDTO> page = new PageImpl<>(Collections.singletonList(payment));

        when(paymentService.getPaymentByPaymentMethod(0, 10, "CREDIT_CARD")).thenReturn(page);

        ResponseEntity<Page<PaymentDTO>> response = paymentController.getPaymentByPaymentMethod("CREDIT_CARD", 0, 10, "ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Page<PaymentDTO> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.getTotalElements());
        assertEquals(1, body.getContent().size());
    }

    @Test
    void testGetPaymentById() {
        PaymentDTO payment = new PaymentDTO();
        payment.setId(1L);

        when(paymentService.getPaymentById(1L)).thenReturn(payment);

        ResponseEntity<PaymentDTO> response = paymentController.getPaymentById(1L, "ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        PaymentDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(1L, body.getId());
    }

    @Test
    void testGetPaymentByOrderId() {
        PaymentDTO payment = new PaymentDTO();
        payment.setOrderId(1L);

        when(paymentService.getPaymentByOrderId(1L)).thenReturn(payment);

        ResponseEntity<PaymentDTO> response = paymentController.getPaymentByOrderId(1L, "ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        PaymentDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(1L, body.getOrderId());
    }

    @Test
    void testGetPaymentByTransactionId() {
        PaymentDTO payment = new PaymentDTO();
        payment.setTransactionId("TXN123");

        when(paymentService.getPaymentByTransactionId("TXN123")).thenReturn(payment);

        ResponseEntity<PaymentDTO> response = paymentController.getPaymentByTransactionId("TXN123", "ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        PaymentDTO body = response.getBody();
        assertNotNull(body);
        assertEquals("TXN123", body.getTransactionId());
    }

    @Test
    void testCreatePayment() {
        CreatePaymentsRequest request = new CreatePaymentsRequest();
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setOrderId(1L);
        payment.setStatus(Payment.Status.PENDING);

        when(paymentService.savePayment(any())).thenReturn(payment);

        ResponseEntity<PaymentDTO> response = paymentController.createPayment(request, "ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        PaymentDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(1L, body.getId());
    }

    @Test
    void testUpdatePayment() {
        UpdatePaymentsRequest request = new UpdatePaymentsRequest();
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setOrderId(1L);
        payment.setStatus(Payment.Status.COMPLETED);

        when(paymentService.updatePayment(eq(1L), any())).thenReturn(payment);

        ResponseEntity<PaymentDTO> response = paymentController.updatePayment(request, 1L, "ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        PaymentDTO body = response.getBody();
        assertNotNull(body);
        assertEquals(1L, body.getId());
    }

    @Test
    void testDeletePayment() {
        doNothing().when(paymentService).deletePayment(1L);

        ResponseEntity<String> response = paymentController.deletePayment(1L, "ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Pagamento deletado com sucesso.", response.getBody());
        verify(paymentService, times(1)).deletePayment(1L);
    }

    @Test
    void testGetAllPayments_UserSuccess() {
        PaymentDTO payment = new PaymentDTO();
        payment.setUserId(1L);
        Page<PaymentDTO> page = new PageImpl<>(Collections.singletonList(payment));

        when(paymentService.getUserById(1L, 0, 10)).thenReturn(page);

        ResponseEntity<Page<PaymentDTO>> response = paymentController.getAllPayments(0, 10, "USER", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Page<PaymentDTO> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.getTotalElements());
        assertEquals(1L, body.getContent().get(0).getUserId());
    }

    @Test
    void testGetAllPayments_Forbidden() {
        ResponseEntity<Page<PaymentDTO>> response = paymentController.getAllPayments(0, 10, "GUEST", 1L);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
