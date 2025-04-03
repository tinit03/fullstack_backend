package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.review;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDto {
  private Long id;
  private int rating;
  private String comment;
  private LocalDateTime createdAt;

  private String buyerName;   // fetches from order.buyer
  private String sellerName;  // fetches from order.item.seller
}
