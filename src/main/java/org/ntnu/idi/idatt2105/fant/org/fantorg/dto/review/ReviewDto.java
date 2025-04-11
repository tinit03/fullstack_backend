package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.review;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO representing a review for an order.
 *
 * <p>This class is used to transfer data related to a review submitted by a buyer or seller for an
 * order. It contains information about the rating, the comment, the time the review was created,
 * and the names of the buyer and the seller associated with the review.
 */
@Getter
@Setter
public class ReviewDto {

  /**
   * The unique identifier of the review.
   *
   * <p>This ID is used to identify the review in the system.
   */
  private Long id;

  /**
   * The rating given in the review.
   *
   * <p>The rating is an integer value that indicates the review score, typically between 0 and 10.
   */
  private int rating;

  /**
   * The comment left by the reviewer about the order.
   *
   * <p>This comment provides additional feedback from the reviewer about the order and the
   * experience.
   */
  private String comment;

  /**
   * The timestamp when the review was created.
   *
   * <p>This field records the date and time when the review was submitted.
   */
  private LocalDateTime createdAt;

  /**
   * The name of the buyer who left the review.
   *
   * <p>This name is fetched from the associated order's buyer details.
   */
  private String buyerName;

  /**
   * The name of the seller associated with the order being reviewed.
   *
   * <p>This name is fetched from the associated order's item's seller details.
   */
  private String sellerName;
}
