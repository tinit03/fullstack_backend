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

@Getter
@Setter
public class ItemCreateDto {
  @NotBlank(message = "Item name is required")
  @Size(max = 80, message = "Item name must be under 80 characters")
  private String itemName;

  @NotBlank(message = "Full description is required")
  @Size(max = 2048, message = "Full description must be under 2048 characters")
  private String description;


  @Digits(integer = 10, fraction = 2, message = "Price format is invalid")
  private BigDecimal price;

  private List<@NotBlank(message = "Tags cannot be blank") String> tags;

  @NotBlank(message = "Postal code is required")
  private String postalCode;

  @NotNull(message = "Subcategory ID is required")
  private Long subcategoryId;

  @NotNull
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private ListingType listingType;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Status status;

  @NotNull
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Condition condition;
  private List<ImageItemUploadDto> images;
  private boolean forSale;



}
