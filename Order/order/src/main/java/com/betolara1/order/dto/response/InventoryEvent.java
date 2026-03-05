package com.betolara1.order.dto.response;

public record InventoryEvent(Long orderId, String sku, Integer quantity, String status) {}
