package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO representing the creation of a review for an order.
 *
 * <p>This class is used to transfer data related to a review that a buyer can submit after
 * completing an order. It includes a rating and an optional comment about the order.
 */
@Getter
@Setter
public class ReviewCreateDto {

  /**
   * The unique identifier of the order being reviewed.
   *
   * <p>This ID links the review to a specific order in the system.
   */
  @NotNull(message = "Order ID is required")
  private Long orderId;

  /**
   * The rating given to the order.
   *
   * <p>The rating is an integer value that must be between 0 and 10, inclusive. A rating of 0 means
   * the lowest rating and 10 represents the highest rating.
   */
  @Min(value = 0, message = "Rating must be at least 1")
  @Max(value = 10, message = "Rating cannot be more than 10")
  private int rating;

  /**
   * An optional comment left by the buyer for the order being reviewed.
   *
   * <p>This comment provides additional feedback about the order.
   */
  private String comment;
}
