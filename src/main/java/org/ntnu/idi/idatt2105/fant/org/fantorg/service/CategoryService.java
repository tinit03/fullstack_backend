package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;

public interface CategoryService {
  Category createCategory(CategoryDto dto);

  Category createSubCategory(SubCategoryDto dto);

  Category updateCategory(Long id, CategoryDto dto);

  Category updateSubCategory(Long id, SubCategoryDto dto);

  void deleteCategory(Long id);
  List<CategoryDto> getAllCategories();
  List<CategoryDto> getAllParentCategories();
  List<SubCategoryDto> getSubCategoriesByParentId(Long parentId);

}
