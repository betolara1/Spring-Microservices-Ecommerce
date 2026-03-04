package com.betolara1.order.service;

import java.time.LocalDateTime;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.betolara1.order.dto.request.SaveOrderRequest;
import com.betolara1.order.dto.request.UpdateOrderRequest;
import com.betolara1.order.dto.response.OrderDTO;
import com.betolara1.order.exception.NotFoundException;
import com.betolara1.order.model.Order;
import com.betolara1.order.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    public OrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    public Page<OrderDTO> getAllOrder(int page, int size){
        Page<Order> orders = orderRepository.findAll(PageRequest.of(page, size));

        if (orders.isEmpty()) {
            throw new NotFoundException("Nenhum pedido registrado.");
        }
        return orders.map(OrderDTO::new);
    }

    public Page<OrderDTO> findByCustomerId(Long customerId, int page, int size){
        Page<Order> orders = orderRepository.findByCustomerId(PageRequest.of(page, size), customerId);

        if (orders.isEmpty()) {
            throw new NotFoundException("Nenhum pedido registrado para o cliente " + customerId);
        }
        return orders.map(OrderDTO::new);
    }

    public Page<OrderDTO> findByStatus(Order.Status status, int page, int size){
        Page<Order> orders = orderRepository.findByStatus(PageRequest.of(page, size), status);

        if (orders.isEmpty()) {
            throw new NotFoundException("Nenhum pedido registrado com o status " + status);
        }
        return orders.map(OrderDTO::new);
    }

    public Page<OrderDTO> findByOrderDate(LocalDate date, int page, int size) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59, 999999999);
        Page<Order> orders = orderRepository.findByOrderDateBetween(PageRequest.of(page, size), start, end);

        if (orders.isEmpty()) {
            throw new NotFoundException("Nenhum pedido registrado na data " + date);
        }
        return orders.map(OrderDTO::new);
    }

    public Order saveOrder(SaveOrderRequest request){
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setOrderDate(request.getOrderDate());
        order.setStatus(request.getStatus());
        order.setTotalAmount(request.getTotalAmount());
        order.setShippingAddress(request.getShippingAddress());
        return orderRepository.save(order);
    }

    public OrderDTO getOrderById(Long id){
        Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Pedido não encontrado com ID: " + id));
        return new OrderDTO(order);
    }

    public Order updateOrder(Long id, UpdateOrderRequest request){
        Order findOrder = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Pedido não encontrado com ID: " + id));
        if(request.getCustomerId() != null){
            findOrder.setCustomerId(request.getCustomerId());
        }
        if(request.getOrderDate() != null){
            findOrder.setOrderDate(request.getOrderDate());
        }
        if(request.getStatus() != null){
            findOrder.setStatus(request.getStatus());
        }
        if(request.getTotalAmount() != null){
            findOrder.setTotalAmount(request.getTotalAmount());
        }
        if(request.getShippingAddress() != null){
            findOrder.setShippingAddress(request.getShippingAddress());
        }

        return orderRepository.save(findOrder);
    }

    public void deleteOrder(Long id){
        Order finOrder = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Pedido não encontrado com ID: " + id));
        orderRepository.delete(finOrder);
    }

}
