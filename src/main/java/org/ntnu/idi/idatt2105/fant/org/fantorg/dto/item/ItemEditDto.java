package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageEditDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Condition;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;

/**
 * Data Transfer Object (DTO) for editing an existing item.
 * <p>
 * This DTO is used when updating or editing the details of an item in the system.
 * It includes fields to modify the name, description, price, tags, images, and other attributes.
 * </p>
 */
@Getter
@Setter
public class ItemEditDto {

  /**
   * The name of the item.
   * <p>
   * This field contains the name of the item, which is required and must not exceed 80 characters.
   * </p>
   */
  @NotBlank(message = "Item name is required")
  @Size(max = 80, message = "Item name must be under 80 characters")
  private String itemName;

  /**
   * A detailed description of the item.
   * <p>
   * This field contains the full description of the item, which is required and must not exceed 2048 characters.
   * </p>
   */
  @NotBlank(message = "Full description is required")
  @Size(max = 2048, message = "Full description must be under 2048 characters")
  private String description;

  /**
   * The price of the item.
   * <p>
   * This field contains the price of the item. It can be left blank when editing if no price change is required.
   * </p>
   */
  private BigDecimal price;

  /**
   * A list of tags associated with the item.
   * <p>
   * This field contains tags describing the item, which cannot be blank.
   * </p>
   */
  private List<@NotBlank(message = "Tags cannot be blank") String> tags;

  /**
   * The postal code associated with the item.
   * <p>
   * This field represents the postal code of the item, and it is required.
   * </p>
   */
  @NotBlank(message = "Postal code is required")
  private String postalCode;

  /**
   * The ID of the subcategory that the item belongs to.
   * <p>
   * This field represents the ID of the subcategory under which the item is categorized.
   * It is required when editing an item.
   * </p>
   */
  @NotNull(message = "Subcategory ID is required")
  private Long subcategoryId;

  /**
   * The listing type of the item.
   * <p>
   * This field indicates the type of the listing (e.g., auction, buy now) and is required.
   * It is formatted as a string.
   * </p>
   */
  @NotNull
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private ListingType listingType;

  /**
   * The status of the item listing.
   * <p>
   * This field represents the status of the item listing (e.g., active, sold) and is required.
   * It is formatted as a string.
   * </p>
   */
  @NotNull
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Status status;

  /**
   * The condition of the item.
   * <p>
   * This field represents the condition of the item (e.g., new, used) and is required.
   * It is formatted as a string.
   * </p>
   */
  @NotNull
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Condition condition;

  /**
   * A list of images associated with the item.
   * <p>
   * This field contains images that are associated with the item. The images can be edited.
   * </p>
   */
  private List<ImageEditDto> images;

  /**
   * A flag indicating whether the item is for sale.
   * <p>
   * This field indicates whether the item is available for sale or not. It is a boolean value.
   * </p>
   */
  private boolean forSale;
}
