package org.ntnu.idi.idatt2105.fant.org.fantorg.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Review;
@Getter
@Setter
public class ItemDetailDto extends ItemDto {
  private String fullDescription;
  private List<Review> reviews;
  private double latitude;
  private double longitude;
  private double averageRating;
  private int reviewCount;
  private boolean isBookmarked;
}
