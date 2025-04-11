package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageItemDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Location;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Condition;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;

/**
 * Data Transfer Object (DTO) for representing an item.
 *
 * <p>This DTO is used to transfer the item data in the system, including item details such as name,
 * description, price, condition, listing type, and more. It also includes additional fields for
 * seller and item images.
 */
@Setter
@Getter
public class ItemDto {

  /**
   * The unique identifier of the item.
   *
   * <p>This field contains the ID of the item in the system.
   */
  private Long id;

  /**
   * The name of the item.
   *
   * <p>This field contains the name of the item.
   */
  private String name;

  /**
   * A detailed description of the item.
   *
   * <p>This field contains the full description of the item.
   */
  private String description;

  /**
   * The price of the item.
   *
   * <p>This field contains the price of the item in the system.
   */
  private BigDecimal price;

  /**
   * A list of tags associated with the item.
   *
   * <p>This field contains the tags describing the item.
   */
  private List<String> tags;

  /**
   * The location of the item.
   *
   * <p>This field represents the location where the item is listed.
   */
  private Location location;

  /**
   * The ID of the category that the item belongs to.
   *
   * <p>This field represents the ID of the main category under which the item is listed.
   */
  private Long categoryId;

  /**
   * The name of the category that the item belongs to.
   *
   * <p>This field represents the name of the main category under which the item is listed.
   */
  private String categoryName;

  /**
   * The ID of the subcategory that the item belongs to.
   *
   * <p>This field represents the ID of the subcategory under which the item is listed.
   */
  private Long subCategoryId;

  /**
   * The name of the subcategory that the item belongs to.
   *
   * <p>This field represents the name of the subcategory under which the item is listed.
   */
  private String subCategoryName;

  /**
   * The timestamp when the item was published.
   *
   * <p>This field represents the date and time when the item was listed in the system.
   */
  private LocalDateTime publishedAt;

  /**
   * The ID of the seller of the item.
   *
   * <p>This field represents the ID of the seller. It can be used to link to the seller's profile.
   */
  private Long sellerId;

  /**
   * The full name of the seller.
   *
   * <p>This field represents the full name of the seller, which can be used to display the seller's
   * name.
   */
  private String sellerFullName;

  /**
   * The profile picture URL of the seller.
   *
   * <p>This field represents the seller's profile picture URL.
   */
  private String sellerPicture;

  /**
   * The listing type of the item.
   *
   * <p>This field represents the type of the listing (e.g., auction, buy now). It is formatted as a
   * string.
   */
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private ListingType listingType;

  /**
   * The condition of the item.
   *
   * <p>This field represents the condition of the item (e.g., new, used). It is formatted as a
   * string.
   */
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Condition condition;

  /**
   * The status of the item listing.
   *
   * <p>This field represents the status of the item listing (e.g., active, sold). It is formatted
   * as a string.
   */
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Status status;

  /**
   * A flag indicating whether the item is for sale.
   *
   * <p>This field is a boolean that indicates if the item is available for sale.
   */
  private boolean forSale;

  /**
   * A list of images associated with the item.
   *
   * <p>This field contains the images that are associated with the item.
   */
  private List<ImageItemDto> images;

  /**
   * A flag indicating whether the item is bookmarked by the user.
   *
   * <p>This field indicates if the item is bookmarked by the current user. Default value is false.
   */
  private Boolean isBookmarked = false;

  /**
   * A flag indicating whether the current user is the owner of the item.
   *
   * <p>This field indicates if the current user is the owner of the item. Default value is false.
   */
  private Boolean isOwner = false;
}
