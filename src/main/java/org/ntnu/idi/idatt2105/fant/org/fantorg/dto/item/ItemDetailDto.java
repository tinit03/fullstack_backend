package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Review;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;

@Getter
@Setter
public class ItemDetailDto extends ItemDto {
  private List<Review> reviews;
  private double averageRating;
  private int reviewCount;
  private boolean isBookmarked;
  private ListingType listingType;
}
