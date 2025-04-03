package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCreateDto {
  @NotNull(message = "Order ID is required")
  private Long orderId;

  @Min(value = 0, message = "Rating must be at least 1")
  @Max(value = 10, message = "Rating cannot be more than 10")
  private int rating;

  private String comment;
}
