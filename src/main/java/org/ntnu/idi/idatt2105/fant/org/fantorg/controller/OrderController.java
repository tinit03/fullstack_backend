package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/order")
@RestController
public class OrderController {
  private final OrderService orderService;

  public OrderController(OrderService orderService){
    this.orderService=orderService;
  }

  @PostMapping("/create")
  public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderCreateDto dto, @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(orderService.createOrder(dto,user));
  }

  @GetMapping()
  public ResponseEntity<List<OrderDto>> getOrders() {
    return ResponseEntity.ok(orderService.getAllOrders());
  }
}
