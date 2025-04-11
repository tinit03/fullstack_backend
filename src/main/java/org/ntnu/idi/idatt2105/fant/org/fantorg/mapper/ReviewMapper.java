package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.review.ReviewCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.review.ReviewDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Review;

/**
 * Utility class for converting between Review entities and their corresponding DTOs.
 * <p>
 * The ReviewMapper class provides methods to map Review entities to ReviewDto and
 * ReviewCreateDto objects, which are used to represent review details in the system.
 * </p>
 */
public class ReviewMapper {

  /**
   * Converts a Review entity to a ReviewDto.
   * <p>
   * This method maps all relevant fields from the Review entity to a ReviewDto,
   * which is used to represent review details in a response.
   * </p>
   *
   * @param review The Review entity to be converted.
   * @return The ReviewDto containing the review details.
   */
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

  /**
   * Converts a ReviewCreateDto to a Review entity.
   * <p>
   * This method maps the information from the ReviewCreateDto to a new Review entity
   * that can be persisted in the database. The order will be set in the service layer.
   * </p>
   *
   * @param dto The ReviewCreateDto to be converted.
   * @return The Review entity containing the review details.
   */
  public static Review toEntity(ReviewCreateDto dto) {
    Review review = new Review();
    review.setRating(dto.getRating());
    review.setComment(dto.getComment());
    // order will be set in the service layer
    return review;
  }
}
