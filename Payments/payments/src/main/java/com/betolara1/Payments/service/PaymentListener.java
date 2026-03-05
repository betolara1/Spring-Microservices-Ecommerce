package com.betolara1.payments.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.betolara1.payments.dto.request.CreatePaymentsRequest;
import com.betolara1.payments.dto.response.PaymentEvent;
import com.betolara1.payments.model.Payment;

@Component
public class PaymentListener {

    private final PaymentService paymentService;
    private final RabbitTemplate rabbitTemplate;
    public PaymentListener(PaymentService paymentService, RabbitTemplate rabbitTemplate) {
        this.paymentService = paymentService;
        this.rabbitTemplate = rabbitTemplate;
    }

    // 1. Ouve a fila "payment.created" (Enviada pelo Order)
    @RabbitListener(queues = "payment.created")
    public void onPaymentCreated(PaymentEvent event) {

        System.out.println("💳 [Payment Service] Requisição de pagamento recebida para o Pedido ID: " + event.orderId());
        System.out.println("💰 Valor a ser cobrado: R$ " + event.totalPrice());

        try {
            // 1. Envia mensagem para o rabbitMQ dizendo que o pagamento está sendo processado
            rabbitTemplate.convertAndSend("ecommerce.exchange", "payment.processing", new PaymentEvent(event.orderId(), event.totalPrice()));

            // 2. Aqui você simula o processamento do pagamento.
            // Exemplo: Salvar o pagamento no banco de dados do microsserviço Payments
            // OBS: Verifique o que o seu paymentService precisa para criar um pagamento!
            
            CreatePaymentsRequest savePayment = new CreatePaymentsRequest();
            savePayment.setOrderId(event.orderId());
            savePayment.setAmount(event.totalPrice());
            System.out.println("✅ Pagamento aprovado com sucesso para o Pedido: " + event.orderId());

            Payment processedPayment = paymentService.savePayment(savePayment);


            // 3. (PRÓXIMO PASSO) Enviar uma nova mensagem para o RabbitMQ dizendo: "Pagamento Aprovado!"
            // Assim o Order pode escutar essa nova mensagem e atualizar o status para PAID.

            if (processedPayment.getStatus() == Payment.Status.COMPLETED) {
                PaymentEvent paymentApprovedEvent = new PaymentEvent(event.orderId(), event.totalPrice());
                rabbitTemplate.convertAndSend("ecommerce.exchange", "payment.ok", paymentApprovedEvent);
            } else {
                PaymentEvent paymentErrorEvent = new PaymentEvent(event.orderId(), event.totalPrice());
                rabbitTemplate.convertAndSend("ecommerce.exchange", "payment.cancel", paymentErrorEvent);
            }

        } catch (Exception e) {
            System.out.println("❌ Erro ao processar pagamento do Pedido: " + event.orderId());

            // IMPORTANTE: Avisa o Order que falhou!
            PaymentEvent paymentErrorEvent = new PaymentEvent(event.orderId(), event.totalPrice());
            rabbitTemplate.convertAndSend("ecommerce.exchange", "payment.error", paymentErrorEvent);
        }
    }
}
