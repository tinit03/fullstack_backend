package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.review.ReviewCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.review.ReviewDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Service interface for managing user reviews. */
public interface ReviewService {

  /**
   * Creates a new review for an order.
   *
   * @param author The user writing the review.
   * @param orderId The ID of the order being reviewed.
   * @param dto The data for the new review.
   */
  void createReview(User author, Long orderId, ReviewCreateDto dto);

  /**
   * Retrieves all reviews written about a specific seller.
   *
   * @param sellerId The ID of the seller.
   * @param pageable Pageable object for pagination.
   * @return A page of ReviewDto objects.
   */
  Page<ReviewDto> getReviewsForSeller(Long sellerId, Pageable pageable);

  /**
   * Calculates the average rating received by a seller.
   *
   * @param seller The seller user.
   * @return The average rating as a double.
   */
  double getAverageRatingForSeller(User seller);

  /**
   * Retrieves the total number of reviews received by a seller.
   *
   * @param seller The seller user.
   * @return The number of reviews.
   */
  long getReviewCountForSeller(User seller);
}
