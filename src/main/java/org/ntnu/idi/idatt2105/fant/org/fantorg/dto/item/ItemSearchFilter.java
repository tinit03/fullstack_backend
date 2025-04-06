package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Condition;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;

@Getter
@Setter
public class ItemSearchFilter {
  private String keyword;
  private List<Long> categoryId;
  private List<Long> subCategoryId;
  private List<Condition> condition;
  private List<String> county;
  private Double minPrice;
  private Double maxPrice;
  private List<ListingType> type;
}
