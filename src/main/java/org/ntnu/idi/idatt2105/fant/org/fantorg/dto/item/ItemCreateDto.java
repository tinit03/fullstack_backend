package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageItemUploadDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Condition;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.ListingType;

/**
 * Data Transfer Object (DTO) for creating an item.
 * <p>
 * This DTO is used to transfer the data required to create a new item in the system.
 * It includes item details such as name, description, price, condition, status, and more.
 * </p>
 */
@Getter
@Setter
public class ItemCreateDto {

  /**
   * The name of the item.
   * <p>
   * This field contains the name of the item. It is a required field and must be less than 80 characters.
   * </p>
   */
  @NotBlank(message = "Item name is required")
  @Size(max = 80, message = "Item name must be under 80 characters")
  private String itemName;

  /**
   * A detailed description of the item.
   * <p>
   * This field contains a full description of the item. It is required and cannot exceed 2048 characters.
   * </p>
   */
  @NotBlank(message = "Full description is required")
  @Size(max = 2048, message = "Full description must be under 2048 characters")
  private String description;

  /**
   * The price of the item.
   * <p>
   * This field contains the price of the item. It must be a valid number with up to 10 digits and 2 decimal places.
   * </p>
   */
  @Digits(integer = 10, fraction = 2, message = "Price format is invalid")
  private BigDecimal price;

  /**
   * A list of tags associated with the item.
   * <p>
   * This field contains tags that describe the item. Each tag cannot be blank.
   * </p>
   */
  private List<@NotBlank(message = "Tags cannot be blank") String> tags;

  /**
   * The postal code associated with the item.
   * <p>
   * This field is required and represents the postal code where the item is located.
   * </p>
   */
  @NotBlank(message = "Postal code is required")
  private String postalCode;

  /**
   * The ID of the subcategory that the item belongs to.
   * <p>
   * This field is required and refers to the subcategory under which the item is listed.
   * </p>
   */
  @NotNull(message = "Subcategory ID is required")
  private Long subcategoryId;

  /**
   * The listing type of the item.
   * <p>
   * This field represents the type of listing for the item (e.g., auction, buy now). It is required.
   * </p>
   */
  @NotNull
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private ListingType listingType;

  /**
   * The status of the item listing.
   * <p>
   * This field represents the current status of the item (e.g., active, sold). It is optional.
   * </p>
   */
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Status status;

  /**
   * The condition of the item.
   * <p>
   * This field represents the condition of the item (e.g., new, used). It is required.
   * </p>
   */
  @NotNull
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Condition condition;

  /**
   * A list of images associated with the item.
   * <p>
   * This field contains images related to the item. It is optional.
   * </p>
   */
  private List<ImageItemUploadDto> images;

  /**
   * A flag indicating whether the item is for sale.
   * <p>
   * This field is used to specify whether the item is available for sale.
   * </p>
   */
  private boolean forSale;



}
