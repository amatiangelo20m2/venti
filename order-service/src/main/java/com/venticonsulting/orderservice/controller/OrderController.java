package com.venticonsulting.orderservice.controller;

import com.venticonsulting.orderservice.dto.OrderRequest;
import com.venticonsulting.orderservice.model.Order;
import com.venticonsulting.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
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

  /**
   * @CircuitBreaker(name = "inventory") --> apply the circuit breaker logic to the inventory call placed inside
   * the method called (placeOrder)
   * @param orderRequest
   * @return
   */
  @PostMapping(path = "/placeorder")
  @ResponseStatus(HttpStatus.CREATED)
  @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
  @TimeLimiter(name = "inventory")
  @Retry(name = "inventory")
  public CompletableFuture<String> placeOrder(@RequestBody OrderRequest orderRequest) {

    return CompletableFuture.supplyAsync(()->  orderService.placeOrder(orderRequest));
  }

  public CompletableFuture<String> fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException){
    return CompletableFuture.supplyAsync(()-> "Ooops, retry in two minutes. The inventory service seems to be down. " +
            "If the error persist, call the system manager");
  }

}
