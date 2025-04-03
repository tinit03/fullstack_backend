package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.review.ReviewCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.review.ReviewDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Review;

public class ReviewMapper {

  public static ReviewDto toDto(Review review) {
    ReviewDto dto = new ReviewDto();
    dto.setId(review.getId());
    dto.setRating(review.getRating());
    dto.setComment(review.getComment());
    dto.setCreatedAt(review.getCreatedAt());

    dto.setBuyerName(review.getOrder().getBuyer().getFirstName() + " " + review.getOrder().getBuyer().getLastName());
    dto.setSellerName(review.getOrder().getItem().getSeller().getFirstName() + " " + review.getOrder().getItem().getSeller().getLastName());

    return dto;
  }

  public static Review toEntity(ReviewCreateDto dto) {
    Review review = new Review();
    review.setRating(dto.getRating());
    review.setComment(dto.getComment());
    // order will be set in the service layer
    return review;
  }
}
