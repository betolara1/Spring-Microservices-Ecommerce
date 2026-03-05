package com.betolara1.order.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.betolara1.order.dto.response.InventoryEvent;
import com.betolara1.order.dto.response.PaymentEvent;
import com.betolara1.order.model.Order;

@Component
public class OrderListener {

    private final OrderService orderService;
    public OrderListener(OrderService orderService) {
        this.orderService = orderService;
    }

    // Método para ouvir o rabbitMQ e atualizar o status do pedido
    @RabbitListener(queues = "payment.ok")
    public void onPaymentOk(PaymentEvent event) {
        System.out.println("✅ [Order Service] Pagamento APROVADO para o Pedido: " + event.orderId());
        orderService.updateStatus(event.orderId(), Order.Status.PAID);
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

    @RabbitListener(queues = "inventory.reserved")
    public void onInventoryReserved(InventoryEvent event) {
        System.out.println("✅ [Order Service] Estoque RESERVADO para o Pedido: " + event.orderId());
        orderService.updateStatus(event.orderId(), Order.Status.RESERVED);
    }

    @RabbitListener(queues = "inventory.outOfStock")
    public void onInventoryOutOfStock(InventoryEvent event) {
        System.out.println("❌ [Order Service] Estoque ESGOTADO para o Pedido: " + event.orderId());
        orderService.updateStatus(event.orderId(), Order.Status.OUT_OF_STOCK);
    }

    @RabbitListener(queues = "inventory.error")
    public void onInventoryError(InventoryEvent event) {
        System.out.println("❌ [Order Service] Erro ao reservar estoque para o Pedido: " + event.orderId());
        orderService.updateStatus(event.orderId(), Order.Status.FAILED);
    }
}
