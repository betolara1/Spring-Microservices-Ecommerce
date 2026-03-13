package com.betolara1.inventory.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.betolara1.inventory.model.Inventory;
import com.betolara1.inventory.dto.request.SaveInventoryRequest;
import com.betolara1.inventory.dto.response.InventoryEvent;
import com.betolara1.inventory.dto.response.ProductEvent;

@Component
public class InventoryListener {

    private static final Logger log = LoggerFactory.getLogger(InventoryListener.class);

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
            Inventory reservedInventory = inventoryService.findBySkuEntity(event.sku());

            if (reservedInventory.getQuantity() >= event.quantity()) {
                InventoryEvent inventoryReservedEvent = new InventoryEvent(event.orderId(), event.sku(), event.quantity(), event.status());
                rabbitTemplate.convertAndSend("ecommerce.exchange", "inventory.reserved", inventoryReservedEvent);
                
                reservedInventory.setQuantity(reservedInventory.getQuantity() - event.quantity());
                inventoryService.updateInventoryEntity(reservedInventory);
                log.info("Estoque reservado com sucesso para SKU: {}, orderId: {}", event.sku(), event.orderId());
            } else {
                InventoryEvent inventoryErrorEvent = new InventoryEvent(event.orderId(), event.sku(), event.quantity(), event.status());
                rabbitTemplate.convertAndSend("ecommerce.exchange", "inventory.outOfStock", inventoryErrorEvent);
                log.warn("Estoque insuficiente para SKU: {}, disponível: {}, solicitado: {}", 
                    event.sku(), reservedInventory.getQuantity(), event.quantity());
            }

        }catch (Exception e){
            log.error("Erro ao reservar estoque para orderId: {}, SKU: {} - {}", 
                event.orderId(), event.sku(), e.getMessage());
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
            log.info("Inventário criado com sucesso para o produto: {} (SKU: {})", event.name(), event.sku());

        }catch (Exception e){
            // Loga o erro com detalhes para facilitar o debug.
            // Não envia evento para RabbitMQ porque ProductEvent não tem orderId —
            // não há pedido envolvido, é apenas a criação automática de estoque.
            log.error("Falha ao criar inventário para o produto: {} (SKU: {}) - {}", 
                event.name(), event.sku(), e.getMessage());
        }
    }
}
