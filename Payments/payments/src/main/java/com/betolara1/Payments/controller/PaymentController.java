package com.betolara1.Payments.controller;

import org.springdoc.core.converters.models.Pageable;
import org.springframework.beans.factory.annotation.Value;
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

import com.betolara1.Payments.dto.response.PaymentDTO;
import com.betolara1.Payments.exception.NotFoundException;
import com.betolara1.Payments.model.Payment;
import com.betolara1.Payments.service.PaymentService;

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



    
    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@Value @RequestBody Payment payment) {
        Payment savedPayment = paymentService.savePayment(payment);
        PaymentDTO paymentDTO = PaymentDTO.createPaymentDTO(savedPayment);
        return ResponseEntity.ok(paymentDTO);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(@RequestBody Long id) {
        PaymentDTO paymentDTO = paymentService.getPaymentById(id);
        return ResponseEntity.ok(paymentDTO);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<PaymentDTO> updatePayment(@RequestBody Payment payment, @PathVariable Long id) {
        Payment updatedPayment = paymentService.updatePayment(id, payment);
        PaymentDTO paymentDTO = PaymentDTO.createPaymentDTO(updatedPayment);
        return ResponseEntity.ok(paymentDTO);
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }


}
