package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item;

import lombok.Getter;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Condition;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;

@Getter
@Setter
public class ItemSearchFilter {
  private String keyword;
  private Long categoryId;
  private Long subCategoryId;
  private Condition condition;
  private String county;
  private Double minPrice;
  private Double maxPrice;
  private ListingType type;
  private String dateSort;
}
