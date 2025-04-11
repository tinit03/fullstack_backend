package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.order.OrderDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.review.ReviewCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.review.ReviewDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing reviews.
 *
 * <p>Provides endpoints for creating a review for an order and retrieving reviews for a specific
 * seller.
 */
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  /**
   * Creates a review for a seller after a completed order.
   *
   * @param orderId the ID of the order for which the review is created
   * @param currentUser the authenticated user creating the review
   * @param reviewCreateDto the DTO containing review details such as content and rating
   * @return a ResponseEntity with HTTP status 200 if the review is created successfully
   */
  @Operation(
      summary = "Create Review",
      description = "Creates a review for a seller associated with a completed order.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Review created successfully",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = OrderDto.class))
            }),
      })
  @PostMapping("/order/{orderId}")
  public ResponseEntity<Void> createReview(
      @Parameter(description = "Order identificator") @PathVariable Long orderId,
      @Parameter(description = "Authenticated user") @AuthenticationPrincipal User currentUser,
      @Parameter(description = "Review creating info") @RequestBody
          ReviewCreateDto reviewCreateDto) {
    reviewService.createReview(currentUser, orderId, reviewCreateDto);
    return ResponseEntity.ok().build();
  }

  /**
   * Retrieves a paginated list of reviews for a specific seller.
   *
   * @param sellerId the ID of the seller whose reviews are to be retrieved
   * @param page the page number for pagination (default is 0)
   * @param size the number of reviews per page (default is 10)
   * @param sortDir the sort direction for reviews by creation date ("asc" or "desc", default is
   *     "desc")
   * @return a ResponseEntity containing a paginated list of ReviewDto objects
   */
  @Operation(
      summary = "Get Reviews for Seller",
      description =
          "Retrieves all reviews for a specific seller in a paginated format. "
              + "Reviews are sorted by creation date in the specified direction.")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Reviews retrieved successfully",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = OrderDto.class))
            }),
      })
  @GetMapping("/seller/{sellerId}")
  public ResponseEntity<Page<ReviewDto>> getReviewsForSeller(
      @Parameter(description = "Seller identification") @PathVariable Long sellerId,
      @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "page size") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "sorting direction")
          @RequestParam(name = "date", defaultValue = "desc")
          String sortDir) {
    Sort sort =
        sortDir.equalsIgnoreCase("asc")
            ? Sort.by("createdAt").ascending()
            : Sort.by("createdAt").descending();
    Pageable pageable = PageRequest.of(page, size, sort);
    Page<ReviewDto> reviews = reviewService.getReviewsForSeller(sellerId, pageable);
    return ResponseEntity.ok(reviews);
  }
}
