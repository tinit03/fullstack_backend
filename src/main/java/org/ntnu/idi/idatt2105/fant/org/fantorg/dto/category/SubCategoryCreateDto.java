package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SubCategoryCreateDto {
  @NotBlank(message = "Subcategory name is required")
  private String name;

  @NotNull(message = "Parent category ID is required")
  private Long parentCategoryId;
}
