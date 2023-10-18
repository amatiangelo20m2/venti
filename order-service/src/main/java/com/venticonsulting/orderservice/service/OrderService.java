package com.venticonsulting.orderservice.service;

import com.venticonsulting.orderservice.dto.InventoryResponse;
import com.venticonsulting.orderservice.dto.OrderLineItemsDto;
import com.venticonsulting.orderservice.dto.OrderRequest;
import com.venticonsulting.orderservice.event.OrderPlacedEvent;
import com.venticonsulting.orderservice.model.Order;
import com.venticonsulting.orderservice.model.OrderLineItems;
import com.venticonsulting.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final WebClient.Builder webClientBuilder;
    public void placeOrder(OrderRequest orderRequest) {
        Order order = Order.builder().build();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

      order.setOrderLineItemsList(orderLineItems);

      List<String> skuCodes = order.getOrderLineItemsList().stream()
          .map(OrderLineItems::getSkuCode).toList();



        // call inventory, place order if products are in stock
        // webClient --> this is the name of the bean that is defined into WebClientConfig class. I can use it to perform rest call

      log.warn("retrieve sku codes: {}", skuCodes);

      InventoryResponse[] inventoryResponses = webClientBuilder.build()
        .get()
        .uri("http://inventory-service/ventimetri/api/inventory",
          uriBuilder -> uriBuilder.queryParam("skuCodes", skuCodes).build())
        .retrieve()
        .bodyToMono(InventoryResponse[].class)
        .block();

      boolean isInStock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);

      if(isInStock){
        orderRepository.save(order);
      }else{
        log.error("not in stock");
      }
    }

    public void create(){
      orderRepository.save(Order.builder().orderNumber("123412312312").build());
    }

  public List<Order> retrieve(){
    return orderRepository.findAll();
  }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
