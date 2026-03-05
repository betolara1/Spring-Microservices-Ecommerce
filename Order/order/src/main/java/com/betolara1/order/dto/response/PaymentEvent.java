package com.betolara1.order.dto.response;

import java.math.BigDecimal;

// DTO para enviar para o rabbitMQ
// Record é uma classe que só serve para guardar dados
public record PaymentEvent(Long orderId, BigDecimal totalPrice) {}
