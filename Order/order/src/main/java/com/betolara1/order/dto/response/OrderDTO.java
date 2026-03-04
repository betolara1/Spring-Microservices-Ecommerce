package com.betolara1.order.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.betolara1.order.model.Order;

@Data
public class OrderDTO {
    private Long id;
    private Long customerId;
    private LocalDateTime orderDate;
    private Order.Status status;
    private BigDecimal totalAmount;
    private String shippingAddress;

    public OrderDTO(){}

    public OrderDTO(Order order){
        this.id = order.getId();
        this.customerId = order.getCustomerId();
        this.orderDate = order.getOrderDate();
        this.status = order.getStatus();
        this.totalAmount = order.getTotalAmount();
        this.shippingAddress = order.getShippingAddress();
    }
}
