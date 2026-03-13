package com.betolara1.order.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.betolara1.order.dto.request.SaveOrderRequest;
import com.betolara1.order.dto.request.UpdateOrderRequest;
import com.betolara1.order.dto.response.OrderDTO;
import com.betolara1.order.exception.NotFoundException;
import com.betolara1.order.model.Order;
import com.betolara1.order.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testGetAllOrder_Success() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(Order.Status.PENDING);

        Page<Order> page = new PageImpl<>(Collections.singletonList(order));
        when(orderRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<OrderDTO> result = orderService.findAllOrder(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetAllOrder_Empty() {
        Page<Order> page = new PageImpl<>(Collections.emptyList());
        when(orderRepository.findAll(any(PageRequest.class))).thenReturn(page);

        assertThrows(NotFoundException.class, () -> orderService.findAllOrder(0, 10));
    }

    @Test
    void testFindByCustomerId_Success() {
        Order order = new Order();
        order.setCustomerId(1L);
        order.setStatus(Order.Status.PENDING);

        Page<Order> page = new PageImpl<>(Collections.singletonList(order));
        when(orderRepository.findByCustomerId(any(PageRequest.class), eq(1L))).thenReturn(page);

        Page<OrderDTO> result = orderService.findByCustomerId(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getCustomerId());
    }

    @Test
    void testFindByCustomerIdAndStatus_Success() {
        Order order = new Order();
        order.setCustomerId(1L);
        order.setStatus(Order.Status.PENDING);

        Page<Order> page = new PageImpl<>(Collections.singletonList(order));
        when(orderRepository.findByCustomerIdAndStatus(any(PageRequest.class), eq(1L), eq(Order.Status.PENDING))).thenReturn(page);

        Page<OrderDTO> result = orderService.findByCustomerIdAndStatus(1L, Order.Status.PENDING, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getCustomerId());
        assertEquals(Order.Status.PENDING, result.getContent().get(0).getStatus());
    }

    @Test
    void testFindByStatus_Success() {
        Order order = new Order();
        order.setStatus(Order.Status.PENDING);

        Page<Order> page = new PageImpl<>(Collections.singletonList(order));
        when(orderRepository.findByStatus(any(PageRequest.class), eq(Order.Status.PENDING))).thenReturn(page);

        Page<OrderDTO> result = orderService.findByStatus(Order.Status.PENDING, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(Order.Status.PENDING, result.getContent().get(0).getStatus());
    }

    @Test
    void testFindByOrderDate_Success() {
        Order order = new Order();
        order.setStatus(Order.Status.PENDING);

        Page<Order> page = new PageImpl<>(Collections.singletonList(order));
        when(orderRepository.findByOrderDateBetween(any(PageRequest.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(page);

        Page<OrderDTO> result = orderService.findByOrderDate(LocalDate.now(), 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetOrderById_Success() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(Order.Status.PENDING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderDTO result = orderService.getOrderById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void testGetOrderByIdAndCustomerId_Success() {
        Order order = new Order();
        order.setId(1L);
        order.setCustomerId(1L);
        order.setStatus(Order.Status.PENDING);

        when(orderRepository.findByIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(order));

        OrderDTO result = orderService.getOrderByIdAndCustomerId(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getCustomerId());
    }

    @Test
    void testGetOrderByIdAndCustomerId_NotFound() {
        when(orderRepository.findByIdAndCustomerId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrderByIdAndCustomerId(1L, 1L));
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrderById(1L));
    }

    @Test
    void testSaveOrder_Success() {
        SaveOrderRequest request = new SaveOrderRequest();
        request.setCustomerId(1L);
        request.setOrderDate(LocalDateTime.now());
        request.setStatus(Order.Status.PENDING);
        request.setTotalAmount(BigDecimal.valueOf(100.0));
        request.setShippingAddress("Test Address");
        request.setSku("SKU123");
        request.setQuantity(2);

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setStatus(Order.Status.PENDING);
        savedOrder.setTotalAmount(BigDecimal.valueOf(100.0));

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));

        Order result = orderService.saveOrder(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(rabbitTemplate, times(1)).convertAndSend(eq("ecommerce.exchange"), eq("payment.created"), (Object) any());
    }

    @Test
    void testUpdateOrder_Success() {
        UpdateOrderRequest request = new UpdateOrderRequest();
        request.setStatus(Order.Status.SHIPPED);

        Order existingOrder = new Order();
        existingOrder.setId(1L);
        existingOrder.setStatus(Order.Status.PENDING);

        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setStatus(Order.Status.SHIPPED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        Order result = orderService.updateOrder(1L, request);

        assertNotNull(result);
        assertEquals(Order.Status.SHIPPED, result.getStatus());
    }

    @Test
    void testUpdateStatus_Success() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(Order.Status.PENDING);

        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setStatus(Order.Status.PROCESSING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        Order result = orderService.updateStatus(1L, Order.Status.PROCESSING);

        assertNotNull(result);
        assertEquals(Order.Status.PROCESSING, result.getStatus());
    }

    @Test
    void testDeleteOrder_Success() {
        Order order = new Order();
        order.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).delete(order);

        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).delete(order);
    }
}
