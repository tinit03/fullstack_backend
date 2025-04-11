package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;

/** Service interface for managing orders. */
public interface OrderService {

  /**
   * Creates a new order.
   *
   * @param createOrder The order creation details.
   * @param user The user placing the order.
   * @return The created order as a DTO.
   */
  OrderDto createOrder(OrderCreateDto createOrder, User user);

  /**
   * Retrieves an order by its ID.
   *
   * @param orderId The ID of the order.
   * @param user The user requesting the order.
   * @return The order as a DTO.
   */
  OrderDto getOrderWithId(long orderId, User user);

  /**
   * Retrieves all orders. Might be restricted to admin use depending on implementation.
   *
   * @return A list of all orders.
   */
  List<OrderDto> getAllOrders();
}
