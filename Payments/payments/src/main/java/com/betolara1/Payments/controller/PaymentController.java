package com.betolara1.Payments.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betolara1.Payments.dto.request.CreatePaymentsRequest;
import com.betolara1.Payments.dto.request.UpdatePaymentsRequest;
import com.betolara1.Payments.dto.response.PaymentDTO;
import com.betolara1.Payments.exception.NotFoundException;
import com.betolara1.Payments.model.Payment;
import com.betolara1.Payments.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private PaymentService paymentService;
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/listAll")
    public ResponseEntity<Page<PaymentDTO>> getAllPayments(            
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<PaymentDTO> list = paymentService.getAllPayments(page, size);
        if (list.isEmpty()) {
            throw new NotFoundException("Nenhum pagamento cadastrado.");
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<PaymentDTO>> getPaymentByStatus(@PathVariable Payment.Status status, 
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<PaymentDTO> list = paymentService.getPaymentByStatus(page, size, status);
        if (list.isEmpty()) {
            throw new NotFoundException("Nenhum pagamento cadastrado.");
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/paymentMethod/{paymentMethod}")
    public ResponseEntity<Page<PaymentDTO>> getPaymentByPaymentMethod(@PathVariable String paymentMethod, 
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
                
        Page<PaymentDTO> list = paymentService.getPaymentByPaymentMethod(page, size, paymentMethod);
        if (list.isEmpty()) {
            throw new NotFoundException("Nenhum pagamento cadastrado.");
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id) {
        PaymentDTO payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/orderId/{orderId}")
    public ResponseEntity<PaymentDTO> getPaymentByOrderId(@PathVariable Long orderId) {
        PaymentDTO payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/transactionId/{transactionId}")
    public ResponseEntity<PaymentDTO> getPaymentByTransactionId(@PathVariable String transactionId) {
        PaymentDTO payment = paymentService.getPaymentByTransactionId(transactionId);
        return ResponseEntity.ok(payment);
    }

    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody CreatePaymentsRequest request) {
        Payment savedPayment = paymentService.savePayment(request);
        PaymentDTO paymentDTO = new PaymentDTO(savedPayment);
        return ResponseEntity.ok(paymentDTO);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<PaymentDTO> updatePayment(@Valid @RequestBody UpdatePaymentsRequest request, @PathVariable Long id) {
        Payment updatedPayment = paymentService.updatePayment(id, request);
        PaymentDTO paymentDTO = new PaymentDTO(updatedPayment);
        return ResponseEntity.ok(paymentDTO);
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<String> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.ok("Pagamento deletado com sucesso.");
    }
}
