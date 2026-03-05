package com.betolara1.order.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.betolara1.order.model.Order;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

@Data
public class UpdateOrderRequest {
    private Long customerId;
    private LocalDateTime orderDate;
    private Order.Status status;

    @DecimalMin(value = "0.01")
    private BigDecimal totalAmount;

    private String shippingAddress;
    private String sku;
    private Integer quantity;

}
