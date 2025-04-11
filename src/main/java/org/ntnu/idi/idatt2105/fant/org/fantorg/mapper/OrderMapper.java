package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Order;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;

/**
 * Utility class for converting between Order entities and their corresponding DTOs.
 * <p>
 * The OrderMapper class provides methods to map Order entities to OrderDto objects
 * for reading order details in the system.
 * </p>
 */
public class OrderMapper {

  /**
   * Converts an Order entity to an OrderDto.
   * <p>
   * This method maps all relevant fields from the Order entity to an OrderDto,
   * which is used to represent order details in a response.
   * </p>
   *
   * @param order The Order entity to be converted.
   * @return The OrderDto containing the order details.
   */
  public static OrderDto toDto(Order order) {
    OrderDto dto = new OrderDto();
    User seller = order.getItem().getSeller();
    dto.setId(order.getId());
    dto.setOrderDate(order.getOrderDate());
    dto.setPrice(order.getPrice());

    dto.setId(order.getId()); // Set the order ID
    dto.setOrderDate(order.getOrderDate()); // Set the order date

    // If the order contains item details, set item ID and title
    if (order.getItem() != null) {
      dto.setItemId(order.getItem().getItemId());
      dto.setItemTitle(order.getItem().getTitle());
    }

    if (order.getBuyer() != null) {
      dto.setBuyerId(order.getBuyer().getId());
      dto.setBuyerName(order.getBuyer().getFirstName() + " " + order.getBuyer().getLastName());
    }

    if (seller != null) {
      dto.setSellerId(seller.getId());
      dto.setSellerName(seller.getFirstName() + " " + seller.getLastName());
    }

    return dto;
  }
}
