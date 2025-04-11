package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Condition;
import org.springframework.data.domain.Page;

/**
 * Data Transfer Object (DTO) used to represent the response of an item search.
 *
 * <p>This DTO is returned after a user performs a search for items. It contains the list of items
 * matching the search criteria along with various facets that help summarize the search results,
 * such as the count of items by condition, availability, county, category, and more.
 */
@Getter
@Setter
public class ItemSearchResponse {
  private Page<ItemDto> items;

  /**
   * A map containing counts of items grouped by condition.
   *
   * <p>This field contains a map where the key is a {@link Condition} and the value is the count of
   * items that match that condition (e.g., new, used).
   */
  private Map<Condition, Long> conditionFacet;

  /**
   * A map containing counts of items grouped by availability status (for sale or not).
   *
   * <p>This field contains a map where the key is a string representing the availability status
   * (e.g., "forSale", "sold") and the value is the count of items in that category.
   */
  private Map<String, Long> forSaleFacet;

  /**
   * A map containing counts of items grouped by county.
   *
   * <p>This field contains a map where the key is a string representing the county and the value is
   * the count of items available in that county.
   */
  private Map<String, Long> countyFacet;

  /**
   * A map containing counts of items grouped by category.
   *
   * <p>This field contains a map where the key is the ID of the category and the value is the count
   * of items belonging to that category.
   */
  private Map<Long, Long> categoryFacet;

  /**
   * A map containing counts of items grouped by subcategory.
   *
   * <p>This field contains a map where the key is the ID of the subcategory and the value is the
   * count of items belonging to that subcategory.
   */
  private Map<Long, Long> subCategoryFacet;

  /**
   * A map containing counts of items grouped by whether they were published today.
   *
   * <p>This field contains a map where the key is a string representing the publication status
   * (e.g., "publishedToday", "notPublishedToday") and the value is the count of items that match
   * that status.
   */
  private Map<String, Long> publishedTodayFacet;
}
