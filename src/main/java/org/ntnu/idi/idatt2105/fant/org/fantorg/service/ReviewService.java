package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.review.ReviewCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.review.ReviewDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
  void createReview(User author, Long orderId, ReviewCreateDto dto);
  Page<ReviewDto> getReviewsForSeller(Long sellerId, Pageable pageable);
  double getAverageRatingForSeller(User seller);
  long getReviewCountForSeller(User seller);
}
