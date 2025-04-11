package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.review.ReviewCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.review.ReviewDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.ReviewMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Order;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Review;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.OrderRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ReviewRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link ReviewService}. Provides functionality for creating reviews, retrieving
 * seller reviews, calculating average ratings, and counting total reviews.
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

  private final OrderRepository orderRepository;
  private final ReviewRepository reviewRepository;

  /**
   * Creates a new review for a completed order.
   *
   * @param author The user submitting the review (must be the buyer).
   * @param orderId The ID of the order being reviewed.
   * @param dto The review data.
   * @throws EntityNotFoundException If the order does not exist.
   * @throws SecurityException If the user is not the buyer of the order.
   * @throws IllegalArgumentException If a review already exists for this order.
   */
  @Override
  public void createReview(User author, Long orderId, ReviewCreateDto dto) {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Order not found"));

    // Optional: Check if user is the buyer of the order
    if (!order.getBuyer().getId().equals(author.getId())) {
      throw new SecurityException("You are not allowed to review this order.");
    }

    // Optional: Prevent duplicate review for same order
    if (reviewRepository.existsByOrder(order)) {
      throw new IllegalArgumentException("This order already has a review.");
    }

    Review review = ReviewMapper.toEntity(dto);
    review.setOrder(order);
    reviewRepository.save(review);
  }

  /**
   * Retrieves a paginated list of reviews for a specific seller.
   *
   * @param sellerId The ID of the seller.
   * @param pageable Pagination information.
   * @return A page of {@link ReviewDto}.
   */
  @Override
  public Page<ReviewDto> getReviewsForSeller(Long sellerId, Pageable pageable) {
    return reviewRepository
        .findAllByOrder_Item_Seller_Id(sellerId, pageable)
        .map(ReviewMapper::toDto);
  }

  /**
   * Calculates the average rating received by a seller.
   *
   * @param seller The seller user.
   * @return The average rating, or 0.0 if there are no reviews.
   */
  @Override
  public double getAverageRatingForSeller(User seller) {
    List<Review> reviews = reviewRepository.findAllByOrder_Item_Seller(seller);
    return reviews.isEmpty()
        ? 0.0
        : reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
  }

  /**
   * Returns the total number of reviews received by a seller.
   *
   * @param seller The seller user.
   * @return The number of reviews.
   */
  @Override
  public long getReviewCountForSeller(User seller) {
    return reviewRepository.countByOrder_Item_Seller(seller);
  }
}
