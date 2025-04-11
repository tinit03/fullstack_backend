package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO representing an order in the system.
 *
 * <p>This class is used to transfer data related to an order. It contains details about the item
 * being ordered, the buyer, the seller, and the order date.
 */
@Getter
@Setter
public class OrderDto {

  /**
   * The unique identifier of the order.
   *
   * <p>This ID is used to reference a specific order in the system.
   */
  private Long id;

  /**
   * The unique identifier of the item being ordered.
   *
   * <p>This ID links the order to a specific item in the system.
   */
  private Long itemId;

  /**
   * The title of the item being ordered.
   *
   * <p>This provides a readable description of the item.
   */
  private String itemTitle;

  /**
   * The unique identifier of the buyer making the order.
   *
   * <p>This ID represents the person who is placing the order.
   */
  private Long buyerId;

  /**
   * The name of the buyer making the order.
   *
   * <p>This represents the full name of the person placing the order.
   */
  private String buyerName;

  /**
   * The unique identifier of the seller of the item.
   *
   * <p>This ID represents the person who is selling the item that is being ordered.
   */
  private Long sellerId;

  /**
   * The name of the seller of the item.
   *
   * <p>This represents the full name of the person selling the item.
   */
  private String sellerName;

  /**
   * The date and time when the order was placed.
   *
   * <p>This timestamp represents when the order was created.
   */
  private LocalDateTime orderDate;

  private BigDecimal price;
}
