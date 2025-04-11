package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category;

import lombok.Getter;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageUploadDto;

/**
 * Data Transfer Object (DTO) for creating a new category.
 *
 * <p>This DTO encapsulates the necessary information required to create a category, including the
 * category name and an associated image.
 *
 * @author Tini Tran
 */
@Getter
@Setter
public class CategoryCreateDto {

  /** The name of the category. */
  private String name;

  /** An {@link ImageUploadDto} representing the image associated with the category. */
  private ImageUploadDto image;
}
