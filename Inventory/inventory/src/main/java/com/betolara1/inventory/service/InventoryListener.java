package com.betolara1.inventory.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.betolara1.inventory.model.Inventory;
import com.betolara1.inventory.dto.request.SaveInventoryRequest;
import com.betolara1.inventory.dto.response.InventoryEvent;
import com.betolara1.inventory.dto.response.ProductEvent;

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
        try{
            // 1. Envia mensagem para o rabbitMQ dizendo que o estoque está sendo reservado
            //rabbitTemplate.convertAndSend("ecommerce.exchange", "inventory.reserved", new InventoryEvent(event.orderId(), event.sku(), event.quantity(), event.status()));

            // 2. Aqui você simula a reserva do estoque.
            // Exemplo: Salvar o estoque no banco de dados do microsserviço Inventory
            // OBS: Verifique o que o seu inventoryService precisa para criar um estoque!

            
            Inventory reservedInventory = inventoryService.findBySkuEntity(event.sku());

            // 3. (PRÓXIMO PASSO) Enviar uma nova mensagem para o RabbitMQ dizendo: "Estoque Reservado!"
            // Assim o Order pode escutar essa nova mensagem e atualizar o status para RESERVED.

            if (reservedInventory.getQuantity() >= event.quantity()) {
                InventoryEvent inventoryReservedEvent = new InventoryEvent(event.orderId(), event.sku(), event.quantity(), event.status());
                rabbitTemplate.convertAndSend("ecommerce.exchange", "inventory.reserved", inventoryReservedEvent);
                
                reservedInventory.setQuantity(reservedInventory.getQuantity() - event.quantity());
                inventoryService.updateInventoryEntity(reservedInventory);
            } else {
                InventoryEvent inventoryErrorEvent = new InventoryEvent(event.orderId(), event.sku(), event.quantity(), event.status());
                rabbitTemplate.convertAndSend("ecommerce.exchange", "inventory.outOfStock", inventoryErrorEvent);
            }

        }catch (Exception e){
            InventoryEvent inventoryErrorEvent = new InventoryEvent(event.orderId(), event.sku(), event.quantity(), event.status());
            rabbitTemplate.convertAndSend("ecommerce.exchange", "inventory.error", inventoryErrorEvent);
        }
    }

    @RabbitListener(queues = "product.created")
    public void onProductCreated(ProductEvent event) {

        try{
            SaveInventoryRequest request = new SaveInventoryRequest();
            request.setSku(event.sku());
            request.setQuantity(0);



            inventoryService.saveInventory(request);
            
        }catch (Exception e){
            
        }
    }
}
