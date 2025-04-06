package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category;

import lombok.Getter;
import lombok.Setter;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageUploadDto;
@Setter
@Getter
public class CategoryCreateDto {
  private String name;
  private ImageUploadDto image;
}
