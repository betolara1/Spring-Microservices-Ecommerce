package com.betolara1.order.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.betolara1.order.dto.request.SaveOrderRequest;
import com.betolara1.order.dto.request.UpdateOrderRequest;
import com.betolara1.order.dto.response.OrderDTO;
import com.betolara1.order.exception.NotFoundException;
import com.betolara1.order.model.Order;
import com.betolara1.order.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Test
    void testGetAllOrder_Success() {
        OrderDTO order = new OrderDTO();
        order.setId(1L);
        order.setCustomerId(1L);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.Status.PENDING);
        order.setTotalAmount(BigDecimal.valueOf(100.0));
        order.setShippingAddress("Test Address");
        order.setSku("SKU123");
        order.setQuantity(2);

        Page<OrderDTO> page = new PageImpl<>(Collections.singletonList(order));

        when(orderService.getAllOrder(0, 10)).thenReturn(page);

        ResponseEntity<Page<OrderDTO>> response = orderController.getAllOrder(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
    }

    @Test
    void testGetOrderByCustomerId_Success() {
        OrderDTO order = new OrderDTO();
        order.setCustomerId(1L);

        Page<OrderDTO> page = new PageImpl<>(Collections.singletonList(order));

        when(orderService.findByCustomerId(1L, 0, 10)).thenReturn(page);

        ResponseEntity<Page<OrderDTO>> response = orderController.getOrderByCustomerId(1L, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getContent().get(0).getCustomerId());
    }

    @Test
    void testGetOrderByStatus_Success() {
        OrderDTO order = new OrderDTO();
        order.setStatus(Order.Status.PENDING);

        Page<OrderDTO> page = new PageImpl<>(Collections.singletonList(order));

        when(orderService.findByStatus(Order.Status.PENDING, 0, 10)).thenReturn(page);

        ResponseEntity<Page<OrderDTO>> response = orderController.getOrderByStatus(Order.Status.PENDING, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Order.Status.PENDING, response.getBody().getContent().get(0).getStatus());
    }

    @Test
    void testGetOrderByOrderDate_Success() {
        OrderDTO order = new OrderDTO();
        LocalDate today = LocalDate.now();

        Page<OrderDTO> page = new PageImpl<>(Collections.singletonList(order));

        when(orderService.findByOrderDate(today, 0, 10)).thenReturn(page);

        ResponseEntity<Page<OrderDTO>> response = orderController.getOrderByOrderDate(today, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetOrderById_Success() {
        OrderDTO order = new OrderDTO();
        order.setId(1L);

        when(orderService.getOrderById(1L)).thenReturn(order);

        ResponseEntity<OrderDTO> response = orderController.getOrderById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testCreateOrder_Success() {
        SaveOrderRequest request = new SaveOrderRequest();
        Order newOrder = new Order();
        newOrder.setId(1L);
        newOrder.setStatus(Order.Status.PENDING);

        when(orderService.saveOrder(any())).thenReturn(newOrder);

        ResponseEntity<OrderDTO> response = orderController.createOrder(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testUpdateOrder_Success() {
        UpdateOrderRequest request = new UpdateOrderRequest();
        Order order = new Order();
        order.setId(1L);
        order.setStatus(Order.Status.PROCESSING);

        when(orderService.updateOrder(eq(1L), any())).thenReturn(order);

        ResponseEntity<OrderDTO> response = orderController.updateOrder(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals(Order.Status.PROCESSING, response.getBody().getStatus());
    }

    @Test
    void testDeleteOrder_Success() {
        doNothing().when(orderService).deleteOrder(1L);

        ResponseEntity<String> response = orderController.deleteOrder(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Pedido deletado com sucesso!", response.getBody());
        verify(orderService, times(1)).deleteOrder(1L);
    }
}
