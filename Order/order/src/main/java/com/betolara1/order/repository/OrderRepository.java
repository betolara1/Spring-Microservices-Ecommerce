package com.betolara1.order.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.betolara1.order.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByStatus(Pageable pageable, Order.Status status);
    Page<Order> findByCustomerId(Pageable pageable, Long customerId);
    Page<Order> findByOrderDateBetween(Pageable pageable, LocalDateTime start, LocalDateTime end);
}