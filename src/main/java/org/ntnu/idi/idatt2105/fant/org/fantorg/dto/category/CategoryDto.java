package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDto {
  private Long id;
  private String name;

  private List<SubCategoryDto> subcategories;
}
