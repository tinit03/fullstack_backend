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

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

  private final OrderRepository orderRepository;
  private final ReviewRepository reviewRepository;
  @Override
  public void createReview(User author, Long orderId, ReviewCreateDto dto) {
    Order order = orderRepository.findById(orderId)
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

  @Override
  public Page<ReviewDto> getReviewsForSeller(Long sellerId, Pageable pageable) {
    return reviewRepository.findAllByOrder_Item_Seller_Id(sellerId, pageable)
        .map(ReviewMapper::toDto);
  }

  @Override
  public double getAverageRatingForSeller(User seller) {
    List<Review> reviews = reviewRepository.findAllByOrder_Item_Seller(seller);
    return reviews.isEmpty()
        ? 0.0
        : reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
  }

  @Override
  public long getReviewCountForSeller(User seller) {
    return reviewRepository.countByOrder_Item_Seller(seller);
  }

}
