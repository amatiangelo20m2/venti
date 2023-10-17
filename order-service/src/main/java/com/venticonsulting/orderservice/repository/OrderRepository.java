package com.venticonsulting.orderservice.repository;


import com.venticonsulting.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
