package com.betolara1.inventory.dto.response;

public record InventoryEvent(Long orderId, String sku, Integer quantity, String status) {}
