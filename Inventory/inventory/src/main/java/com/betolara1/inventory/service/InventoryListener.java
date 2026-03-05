package com.betolara1.inventory.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.betolara1.inventory.model.Inventory;
import com.betolara1.inventory.dto.request.SaveInventoryRequest;
import com.betolara1.inventory.dto.response.InventoryEvent;

@Component
public class InventoryListener {

    private final InventoryService inventoryService;
    private final RabbitTemplate rabbitTemplate;
    public InventoryListener(InventoryService inventoryService, RabbitTemplate rabbitTemplate) {
        this.inventoryService = inventoryService;
        this.rabbitTemplate = rabbitTemplate;
    }

    // Método para ouvir o rabbitMQ e atualizar o status do pedido
    @RabbitListener(queues = "inventory.created")
    public void onInventoryCreated(InventoryEvent event) {
        System.out.println("💳 [Inventory Service] Requisição de estoque recebida para o Pedido ID: " + event.orderId());
        System.out.println("💰 Quantidade a ser reservada: " + event.quantity());

        try{
            // 1. Envia mensagem para o rabbitMQ dizendo que o estoque está sendo reservado
            //rabbitTemplate.convertAndSend("ecommerce.exchange", "inventory.reserved", new InventoryEvent(event.orderId(), event.sku(), event.quantity(), event.status()));

            // 2. Aqui você simula a reserva do estoque.
            // Exemplo: Salvar o estoque no banco de dados do microsserviço Inventory
            // OBS: Verifique o que o seu inventoryService precisa para criar um estoque!

            SaveInventoryRequest saveRequest = new SaveInventoryRequest();
            saveRequest.setSku(event.sku());
            saveRequest.setQuantity(event.quantity());

            Inventory reservedInventory = inventoryService.saveInventory(saveRequest);
            System.out.println("✅ Estoque reservado com sucesso para o Pedido: " + event.orderId());

            // 3. (PRÓXIMO PASSO) Enviar uma nova mensagem para o RabbitMQ dizendo: "Estoque Reservado!"
            // Assim o Order pode escutar essa nova mensagem e atualizar o status para RESERVED.

            if (reservedInventory.getQuantity() > 0) {
                InventoryEvent inventoryReservedEvent = new InventoryEvent(event.orderId(), event.sku(), event.quantity(), event.status());
                rabbitTemplate.convertAndSend("ecommerce.exchange", "inventory.reserved", inventoryReservedEvent);
            } else {
                InventoryEvent inventoryErrorEvent = new InventoryEvent(event.orderId(), event.sku(), event.quantity(), event.status());
                rabbitTemplate.convertAndSend("ecommerce.exchange", "inventory.outOfStock", inventoryErrorEvent);
            }

        }catch (Exception e){
            System.out.println("❌ Erro ao reservar estoque do Pedido: " + event.orderId());
            InventoryEvent inventoryErrorEvent = new InventoryEvent(event.orderId(), event.sku(), event.quantity(), event.status());
            rabbitTemplate.convertAndSend("ecommerce.exchange", "inventory.error", inventoryErrorEvent);
        }
    }
}
