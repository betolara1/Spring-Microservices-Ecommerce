package com.betolara1.payments.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betolara1.payments.dto.request.CreatePaymentsRequest;
import com.betolara1.payments.dto.request.UpdatePaymentsRequest;
import com.betolara1.payments.dto.response.PaymentDTO;
import com.betolara1.payments.exception.NotFoundException;
import com.betolara1.payments.model.Payment;
import com.betolara1.payments.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<Page<PaymentDTO>> getAllPayments(            
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") Long userId) {

        if(role.equals("ADMIN")){
            Page<PaymentDTO> list = paymentService.getAllPayments(page, size);
            if (list.isEmpty()) {
                throw new NotFoundException("Nenhum pagamento cadastrado.");
            }
            return ResponseEntity.ok(list);
        }
        else if(role.equals("USER")){
            Page<PaymentDTO> list = paymentService.getUserById(userId, page, size);
            if (list.isEmpty()) {
                throw new NotFoundException("Nenhum pagamento cadastrado.");
            }
            return ResponseEntity.ok(list);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/status={status}")
    public ResponseEntity<Page<PaymentDTO>> getPaymentByStatus(
            @PathVariable Payment.Status status, 
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestHeader("X-User-Role") String role) {

        if(role.equals("ADMIN")){
            Page<PaymentDTO> list = paymentService.getPaymentByStatus(page, size, status);
            if (list.isEmpty()) {
                throw new NotFoundException("Nenhum pagamento cadastrado.");
            }
            return ResponseEntity.ok(list);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/paymentMethod={paymentMethod}")
    public ResponseEntity<Page<PaymentDTO>> getPaymentByPaymentMethod(
            @PathVariable String paymentMethod, 
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestHeader("X-User-Role") String role) {
                
        if(role.equals("ADMIN")){
            Page<PaymentDTO> list = paymentService.getPaymentByPaymentMethod(page, size, paymentMethod);
            if (list.isEmpty()) {
                throw new NotFoundException("Nenhum pagamento cadastrado.");
            }
            return ResponseEntity.ok(list);
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/id={id}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id, @RequestHeader("X-User-Role") String role) {
        if(role.equals("ADMIN")){
            PaymentDTO payment = paymentService.getPaymentById(id);
            return ResponseEntity.ok(payment);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/orderId={orderId}")
    public ResponseEntity<PaymentDTO> getPaymentByOrderId(@PathVariable Long orderId, @RequestHeader("X-User-Role") String role) {
        if(role.equals("ADMIN")){
            PaymentDTO payment = paymentService.getPaymentByOrderId(orderId);
            return ResponseEntity.ok(payment);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/transactionId={transactionId}")
    public ResponseEntity<PaymentDTO> getPaymentByTransactionId(@PathVariable String transactionId, @RequestHeader("X-User-Role") String role) {
        if(role.equals("ADMIN")){
            PaymentDTO payment = paymentService.getPaymentByTransactionId(transactionId);
            return ResponseEntity.ok(payment);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody CreatePaymentsRequest request, @RequestHeader("X-User-Role") String role) {
        if(role.equals("ADMIN")){
            Payment savedPayment = paymentService.savePayment(request);
            PaymentDTO paymentDTO = new PaymentDTO(savedPayment);
            return ResponseEntity.status(HttpStatus.CREATED).body(paymentDTO);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDTO> updatePayment(@Valid @RequestBody UpdatePaymentsRequest request, @PathVariable Long id, @RequestHeader("X-User-Role") String role) {
        if(role.equals("ADMIN")){
            Payment updatedPayment = paymentService.updatePayment(id, request);
            PaymentDTO paymentDTO = new PaymentDTO(updatedPayment);
            return ResponseEntity.ok(paymentDTO);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePayment(@PathVariable Long id, @RequestHeader("X-User-Role") String role) {
        if(role.equals("ADMIN")){
            paymentService.deletePayment(id);
            return ResponseEntity.ok("Pagamento deletado com sucesso.");
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
