package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Condition;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class ItemSearchResponse {
  private Page<ItemDto> items;
  private Map<Condition, Long> conditionFacet;
  private Map<ListingType, Long> listingTypeFacet;
  private Map<String, Long> countyFacet;
  private Map<Long, Long> categoryFacet;
  private Map<Long, Long> subCategoryFacet;
}
