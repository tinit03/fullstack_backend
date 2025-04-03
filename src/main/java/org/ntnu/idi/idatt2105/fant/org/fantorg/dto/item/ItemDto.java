package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Location;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Review;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Condition;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;


@Setter
@Getter
public class ItemDto {
  private Long id;
  private String name;
  private String description;
  private BigDecimal price;
  private List<String> tags;
  private Location location;
  private Long categoryId;
  private String categoryName;
  private Long subCategoryId;
  private String subCategoryName;
  private LocalDateTime publishedAt;
  private Long sellerId;         // om vi ønsker å henvise bruker til selgerens brukerside
  private String sellerFullName; // om vi ønsker å vise selger sin brukernavn
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private ListingType listingType;
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Condition condition;
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Status status;
  private boolean forSale;
  private List<ImageDto> images;
  private Boolean isBookmarked = false;
}
