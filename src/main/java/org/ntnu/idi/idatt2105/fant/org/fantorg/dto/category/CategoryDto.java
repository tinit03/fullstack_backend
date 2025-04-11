package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageDto;

/**
 * Data Transfer Object (DTO) representing a category.
 *
 * <p>This class encapsulates the information related to a category including its unique identifier,
 * name, associated subcategories, and an image representation.
 *
 * @author Tini Tran
 */
@Getter
@Setter
public class CategoryDto {

  /** The unique identifier for the category. */
  private Long id;

  /** The name of the category. */
  private String name;

  /**
   * A list of subcategories associated with the category.
   *
   * <p>Each subcategory is represented by a {@code SubCategoryDto} object.
   */
  private List<SubCategoryDto> subcategories;

  /**
   * The image associated with the category.
   *
   * <p>This is represented by an {@link ImageDto} object.
   */
  private ImageDto image;
}
