package com.betolara1.order.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.betolara1.order.dto.response.InventoryEvent;
import com.betolara1.order.dto.response.PaymentEvent;
import com.betolara1.order.model.Order;

@Component
public class OrderListener {

    private final OrderService orderService;
    private final RabbitTemplate rabbitTemplate;
    public OrderListener(OrderService orderService, RabbitTemplate rabbitTemplate) {
        this.orderService = orderService;
        this.rabbitTemplate = rabbitTemplate;
    }

    // Método para ouvir o rabbitMQ e atualizar o status do pedido
    @RabbitListener(queues = "payment.ok")
    public void onPaymentOk(PaymentEvent event) {
        System.out.println("✅ [Order Service] Pagamento APROVADO para o Pedido: " + event.orderId() + "\n Iniciando reserva de estoque:");

        //Atualiza status e pega os dados do pedido (SKU, Qtd)
        Order order = orderService.updateStatus(event.orderId(), Order.Status.PAID);

        InventoryEvent inventoryEvent = new InventoryEvent(order.getId(), order.getSku(), order.getQuantity(), order.getStatus().name());

        rabbitTemplate.convertAndSend("ecommerce.exchange", "inventory.created", inventoryEvent);
    }

    // Método para ouvir o rabbitMQ e atualizar o status do pedido
    @RabbitListener(queues = "payment.error")
    public void onPaymentError(PaymentEvent event) {
        System.out.println("❌ [Order Service] Pagamento RECUSADO para o Pedido: " + event.orderId());
        orderService.updateStatus(event.orderId(), Order.Status.FAILED);
    }

    @RabbitListener(queues = "payment.processing")
    public void onPaymentProcessing(PaymentEvent event) {
        System.out.println("⏳ [Order Service] Pagamento em PROCESSAMENTO para o Pedido: " + event.orderId());
        orderService.updateStatus(event.orderId(), Order.Status.PROCESSING);
    }

    @RabbitListener(queues = "payment.cancel")
    public void onPaymentCancel(PaymentEvent event) {
        System.out.println("❌ [Order Service] Pagamento CANCELADO para o Pedido: " + event.orderId());
        orderService.updateStatus(event.orderId(), Order.Status.CANCELLED);
    }

    @RabbitListener(queues = "payment.refund")
    public void onPaymentRefund(PaymentEvent event) {
        System.out.println("❌ [Order Service] Pagamento REEMBOLSADO para o Pedido: " + event.orderId());
        orderService.updateStatus(event.orderId(), Order.Status.REFUND);
    }

    @RabbitListener(queues = "inventory.reserved")
    public void onInventoryReserved(InventoryEvent event) {
        System.out.println("✅ [Order Service] Estoque RESERVADO para o Pedido: " + event.orderId());
        orderService.updateStatus(event.orderId(), Order.Status.APPROVED);
    }

    @RabbitListener(queues = "inventory.outOfStock")
    public void onInventoryOutOfStock(InventoryEvent event) {
        System.out.println("❌ [Order Service] Estoque ESGOTADO para o Pedido: " + event.orderId());
        orderService.updateStatus(event.orderId(), Order.Status.OUT_OF_STOCK);
        
        PaymentEvent refundEvent = new PaymentEvent(event.orderId(), null);
        
        rabbitTemplate.convertAndSend("ecommerce.exchange", "payment.refund", refundEvent);
    }

    @RabbitListener(queues = "inventory.error")
    public void onInventoryError(InventoryEvent event) {
        System.out.println("❌ [Order Service] Erro ao reservar estoque para o Pedido: " + event.orderId());
        orderService.updateStatus(event.orderId(), Order.Status.FAILED);
    }
}
