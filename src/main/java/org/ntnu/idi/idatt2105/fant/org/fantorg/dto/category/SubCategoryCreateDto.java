package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for creating a new subcategory.
 *
 * <p>This DTO encapsulates the necessary information required to create a subcategory, including
 * the subcategory name and the identifier of its parent category.
 *
 * @author Tini Tran
 */
@Getter
@Setter
public class SubCategoryCreateDto {

  /**
   * The name of the subcategory.
   *
   * <p>This field is required and cannot be blank.
   */
  @NotBlank(message = "Subcategory name is required")
  private String name;

  /**
   * The identifier of the parent category.
   *
   * <p>This field is required and must not be {@code null}.
   */
  @NotNull(message = "Parent category ID is required")
  private Long parentCategoryId;
}
