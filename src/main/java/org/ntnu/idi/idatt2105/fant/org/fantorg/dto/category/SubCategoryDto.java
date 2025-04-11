package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for representing a subcategory.
 * <p>
 * This DTO encapsulates the details of a subcategory, including its unique identifier,
 * name, and the identifier of its parent category.
 * </p>
 *
 * @author Tini Tran
 */
@Getter
@Setter
public class SubCategoryDto {

  /**
   * The unique identifier of the subcategory.
   */
  private Long id;

  /**
   * The name of the subcategory.
   * <p>
   * This field is required and must not be blank.
   * </p>
   */
  @NotBlank(message = "Subcategory name is required")
  private String name;

  /**
   * The identifier of the parent category to which this subcategory belongs.
   * <p>
   * This field is required and must not be null.
   * </p>
   */
  @NotNull(message = "Parent category ID is required")
  private Long parentCategoryId;
}
