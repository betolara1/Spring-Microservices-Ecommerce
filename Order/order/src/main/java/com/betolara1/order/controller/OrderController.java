package com.betolara1.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.format.annotation.DateTimeFormat;

import com.betolara1.order.dto.request.SaveOrderRequest;
import com.betolara1.order.dto.request.UpdateOrderRequest;
import com.betolara1.order.dto.response.OrderDTO;
import com.betolara1.order.model.Order;
import com.betolara1.order.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<Page<OrderDTO>> getAllOrder(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") Long userId) {

        if(role.equals("ADMIN")){
            Page<OrderDTO> orderDTO = orderService.findAllOrder(page, size);
            return ResponseEntity.ok(orderDTO);
        }
        else if(role.equals("USER")){
            Page<OrderDTO> orderDTO = orderService.findByCustomerId(userId, page, size);
            return ResponseEntity.ok(orderDTO);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/customerId={customerId}")
    public ResponseEntity<Page<OrderDTO>> getOrderByCustomerId(
            @PathVariable Long customerId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestHeader("X-User-Role") String role) {

        if(role.equals("ADMIN")){
            Page<OrderDTO> orderDTO = orderService.findByCustomerId(customerId, page, size);
            return ResponseEntity.ok(orderDTO);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/status={status}")
    public ResponseEntity<Page<OrderDTO>> getOrderByStatus(
            @PathVariable Order.Status status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") Long userId) {

        
        if(role.equals("ADMIN")){
            Page<OrderDTO> orderDTO = orderService.findByStatus(status, page, size);
            return ResponseEntity.ok(orderDTO);
        }
        else if(role.equals("USER")){
            Page<OrderDTO> orderDTO = orderService.findByCustomerIdAndStatus(userId, status, page, size);
            return ResponseEntity.ok(orderDTO);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/orderDate={orderDate}")
    public ResponseEntity<Page<OrderDTO>> getOrderByOrderDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate orderDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role) {

        if(role.equals("ADMIN")){
            Page<OrderDTO> orderDTO = orderService.findByOrderDate(orderDate, page, size);
            return ResponseEntity.ok(orderDTO);
        }
        else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/id={id}")
    public ResponseEntity<OrderDTO> getOrderById(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") Long userId){

        if(role.equals("ADMIN")){
            OrderDTO orderId = orderService.getOrderById(id);
            return ResponseEntity.ok(orderId);
        }
        else if(role.equals("USER")){
            OrderDTO orderId = orderService.getOrderByIdAndCustomerId(id, userId);
            return ResponseEntity.ok(orderId);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @Valid @RequestBody SaveOrderRequest request, 
            @RequestHeader("X-User-Id") Long userId){// <-- Lendo o ID que o Gateway colocou!
                                            
        // SEGURANÇA: Garante que o pedido está sendo gerado para o cliente que está logado
        request.setCustomerId(userId);
        Order newOrder = orderService.saveOrder(request);
        OrderDTO orderDTO = new OrderDTO(newOrder);

        return ResponseEntity.status(HttpStatus.CREATED).body(orderDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(
            @PathVariable Long id, 
            @Valid @RequestBody UpdateOrderRequest request,
            @RequestHeader("X-User-Role") String role){
                
        if(role.equals("ADMIN")){
            Order orderDb = orderService.updateOrder(id, request);
            OrderDTO orderDTO = new OrderDTO(orderDb);
            return ResponseEntity.ok(orderDTO);
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id, @RequestHeader("X-User-Role") String role){
        if(role.equals("ADMIN")){
            orderService.deleteOrder(id);
            return ResponseEntity.ok("Pedido deletado com sucesso!");
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

}
