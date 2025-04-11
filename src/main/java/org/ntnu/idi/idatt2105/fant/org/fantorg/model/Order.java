package org.ntnu.idi.idatt2105.fant.org.fantorg.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an order placed by a buyer for an item.
 * <p>
 * This entity captures the details of an order, including the item being ordered, the buyer placing the order,
 * and the date and time the order was created. Orders are placed when a buyer decides to purchase an item.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"Orders\"")
public class Order {

  /**
   * The unique identifier for the order.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The item being purchased in the order.
   * This is a reference to the `Item` entity, indicating which item has been ordered.
   */
  @ManyToOne
  @JoinColumn(name = "item_id")
  private Item item;

  /**
   * The buyer who placed the order.
   * This is a reference to the `User` entity, indicating which user made the purchase.
   */
  @ManyToOne
  @JoinColumn(name = "buyer_id")
  private User buyer;

  /**
   * The date and time when the order was placed.
   * This is set to the timestamp when the order is created.
   */
  private LocalDateTime orderDate;
}
