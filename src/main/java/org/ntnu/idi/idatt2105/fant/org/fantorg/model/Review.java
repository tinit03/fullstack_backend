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
 * Represents a review given by a buyer for a purchased item.
 * <p>
 * This entity stores the rating and comment given by the buyer in relation to an order.
 * It is linked to the `Order` entity, representing the specific order for which the review was provided.
 * </p>
 */
@NoArgsConstructor
@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "\"Reviews\"")
public class Review {

  /**
   * The unique identifier for the review.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The rating for the item, typically between 1 and 5 stars.
   * This rating helps in evaluating the quality or satisfaction with the purchased item.
   */
  private int rating; // 1-5 stars

  /**
   * The comment provided by the buyer as part of the review.
   * This comment typically elaborates on the rating and provides feedback on the item or the purchasing experience.
   */
  private String comment;

  /**
   * The date and time when the review was created.
   * This helps track when the review was submitted.
   */
  private LocalDateTime createdAt = LocalDateTime.now();

  /**
   * The order associated with the review.
   * This relationship links the review to a specific purchase made by the user.
   */
  @ManyToOne
  @JoinColumn(name = "order_id", nullable = false, unique = true)
  private Order order;
}
