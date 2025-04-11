package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) used to represent the search filters for items.
 *
 * <p>This DTO is used to capture the criteria provided by the user to filter the list of items
 * based on various attributes such as category, price, condition, and more.
 */
@Getter
@Setter
public class ItemSearchFilter {

  /**
   * The search keyword.
   *
   * <p>This field contains a keyword that can be used to search for items by name or description.
   * It allows for partial matching of item names or descriptions.
   */
  private String keyword;

  /**
   * The ID of the category to filter by.
   *
   * <p>This field represents the ID of the category by which the user wants to filter items. If not
   * provided, no category filter will be applied.
   */
  private String categoryId;

  /**
   * The ID of the subcategory to filter by.
   *
   * <p>This field represents the ID of the subcategory by which the user wants to filter items. If
   * not provided, no subcategory filter will be applied.
   */
  private String subCategoryId;

  /**
   * The condition of the item to filter by.
   *
   * <p>This field represents the condition of the item (e.g., new, used) to filter by. If not
   * provided, no condition filter will be applied.
   */
  private String condition;

  /**
   * The county to filter the items by.
   *
   * <p>This field represents the county location for which the user wants to filter items. If not
   * provided, no location filter will be applied.
   */
  private String county;

  /**
   * The minimum price to filter the items by.
   *
   * <p>This field represents the minimum price the item must have to be included in the search
   * results. If not provided, no minimum price filter will be applied.
   */
  private Double minPrice;

  /**
   * The maximum price to filter the items by.
   *
   * <p>This field represents the maximum price the item can have to be included in the search
   * results. If not provided, no maximum price filter will be applied.
   */
  private Double maxPrice;

  /**
   * Whether to filter items that are for sale.
   *
   * <p>This field represents whether the user wants to filter items that are for sale (true) or not
   * for sale (false). If not provided, no for-sale filter will be applied.
   */
  private Boolean forSale;

  /**
   * Whether to only show items that are listed today.
   *
   * <p>This field represents whether the user wants to filter items that have been listed today. If
   * not provided, no filter for recent listings will be applied.
   */
  private Boolean onlyToday;
}
