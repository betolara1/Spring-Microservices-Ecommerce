package com.betolara1.order.service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.betolara1.order.dto.request.SaveOrderRequest;
import com.betolara1.order.dto.request.UpdateOrderRequest;
import com.betolara1.order.dto.response.OrderDTO;
import com.betolara1.order.dto.response.PaymentEvent;
import com.betolara1.order.exception.NotFoundException;
import com.betolara1.order.model.Order;
import com.betolara1.order.repository.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    public OrderService(OrderRepository orderRepository, RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public Page<OrderDTO> findAllOrder(int page, int size){
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

    public Page<OrderDTO> findByCustomerIdAndStatus(Long customerId, Order.Status status, int page, int size){
        Page<Order> orders = orderRepository.findByCustomerIdAndStatus(PageRequest.of(page, size), customerId, status);

        if (orders.isEmpty()) {
            throw new NotFoundException("Nenhum pedido registrado com o status " + status + " para o cliente " + customerId);
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

    public OrderDTO getOrderById(Long id){
        Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Pedido não encontrado com ID: " + id));
        return new OrderDTO(order);
    }

    public OrderDTO getOrderByIdAndCustomerId(Long id, Long customerId){
        Optional<Order> order = orderRepository.findByIdAndCustomerId(id, customerId);
        if (order.isEmpty()) {
            throw new NotFoundException("Pedido não encontrado com ID: " + id + " para o cliente " + customerId);
        }
        return new OrderDTO(order.get());
    }

    // Método para salvar o pedido e enviar para o rabbitMQ
    @Transactional
    public Order saveOrder(SaveOrderRequest request) {

        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setOrderDate(request.getOrderDate());
        order.setStatus(request.getStatus());
        order.setTotalAmount(request.getTotalAmount());
        order.setShippingAddress(request.getShippingAddress());
        order.setSku(request.getSku());
        order.setQuantity(request.getQuantity());

        order.setStatus(Order.Status.PENDING); // Ainda não pagou
        order = orderRepository.save(order); // update object with generated ID

        // 2. Cria o objeto ("A Carta") que vai enviar. (Usa um DTO simplificado, não a Entidade)
        PaymentEvent event = new PaymentEvent(order.getId(), order.getTotalAmount());

        // 3. Joga no Correio (Nome da Exchange, Etiqueta, O Pacote JSON)
        rabbitTemplate.convertAndSend("ecommerce.exchange", "payment.created", event);

        return order;
    }

    @Transactional
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

    @Transactional
    public void deleteOrder(Long id){
        Order finOrder = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Pedido não encontrado com ID: " + id));
        orderRepository.delete(finOrder);
    }

    // Método para atualizar o status do pedido
    // Usado pelo OrderListener para atualizar o status do pedido
    @Transactional
    public Order updateStatus(Long id, Order.Status status) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Pedido não encontrado com ID: " + id));
        order.setStatus(status);
        return orderRepository.save(order);
    }

}
