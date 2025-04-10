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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * REST controller for managing orders.
 * <p>
 * Provides endpoints to create a new order and retrieve all orders.
 * </p>
 */
@RestController
@RequestMapping("/order")
public class OrderController {

  private final OrderService orderService;

  /**
   * Constructs an OrderController with the specified OrderService.
   *
   * @param orderService the service used to manage order operations
   */
  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  /**
   * Creates a new order using the provided order details and authenticated user information.
   *
   * @param dto the OrderCreateDto containing the details for creating the order
   * @param user the authenticated user creating the order
   * @return a ResponseEntity containing the created OrderDto
   */
  @Operation(
      summary = "Create Order",
      description = "Creates a new order using the provided order details and authenticated user."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Order created successfully")
  })
  @PostMapping("/create")
  public ResponseEntity<OrderDto> createOrder(
      @Valid @RequestBody OrderCreateDto dto,
      @AuthenticationPrincipal User user) {
    return ResponseEntity.ok(orderService.createOrder(dto, user));
  }

  /**
   * Retrieves a list of all orders.
   *
   * @return a ResponseEntity containing a list of OrderDto objects representing all orders
   */
  @Operation(
      summary = "Get Orders",
      description = "Retrieves a list of all orders."
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
  })
  @GetMapping
  public ResponseEntity<List<OrderDto>> getOrders() {
    return ResponseEntity.ok(orderService.getAllOrders());
  }
}
