package com.venticonsulting.orderservice.controller;

import com.venticonsulting.orderservice.dto.OrderRequest;
import com.venticonsulting.orderservice.model.Order;
import com.venticonsulting.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest) {

      orderService.placeOrder(orderRequest);
      return "Order placed successfully";
    }


}
