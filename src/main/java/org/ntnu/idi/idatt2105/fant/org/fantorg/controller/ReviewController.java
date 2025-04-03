package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  /**
   * Create a review for a seller after a completed order.
   */
  @PostMapping("/order/{orderId}")
  public ResponseEntity<Void> createReview(
      @PathVariable Long orderId,
      @AuthenticationPrincipal User currentUser,
      @RequestBody ReviewCreateDto reviewCreateDto) {
    reviewService.createReview(currentUser, orderId, reviewCreateDto);
    return ResponseEntity.ok().build();
  }

  /**
   * Get all reviews for a specific seller.
   */
  @GetMapping("/seller/{sellerId}")
  public ResponseEntity<Page<ReviewDto>> getReviewsForSeller(
      @PathVariable Long sellerId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(name = "date", defaultValue = "desc") String sortDir
  ) {
    Sort sort = sortDir.equalsIgnoreCase("asc")
        ? Sort.by("createdAt").ascending()
        : Sort.by("createdAt").descending();
    Pageable pageable = PageRequest.of(page, size,sort);
    Page<ReviewDto> reviews = reviewService.getReviewsForSeller(sellerId, pageable);
    return ResponseEntity.ok(reviews);
  }
}

