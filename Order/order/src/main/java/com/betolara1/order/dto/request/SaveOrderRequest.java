package com.betolara1.order.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.betolara1.order.model.Order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaveOrderRequest {
    private Long customerId;

    @NotNull(message = "Order date is required")
    private LocalDateTime orderDate;

    @NotNull(message = "Status is required")
    private Order.Status status;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.01")
    private BigDecimal totalAmount;

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotNull(message = "Quantity is required")
    private Integer quantity;
}
