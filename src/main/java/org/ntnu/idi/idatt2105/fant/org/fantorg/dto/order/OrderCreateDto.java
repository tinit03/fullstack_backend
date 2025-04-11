package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO representing the creation of a new order.
 * <p>
 * This class is used to transfer data when creating a new order. It contains the necessary information
 * to associate an order with a specific item. The only required field in this DTO is the item ID.
 * </p>
 */
@Getter
@Setter
public class OrderCreateDto {

  /**
   * The unique identifier of the item being ordered.
   * <p>
   * This ID links the order to a specific item in the system. The item is required to be present
   * when creating an order.
   * </p>
   */
  @NotNull(message = "Item ID is required")
  private Long itemId;
}
