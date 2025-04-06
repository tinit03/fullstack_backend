package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;

public interface CategoryService {
  Category createCategory(CategoryCreateDto dto);

  Category createSubCategory(SubCategoryCreateDto dto);

  Category updateCategory(Long id, CategoryCreateDto dto);

  Category updateSubCategory(Long id, SubCategoryDto dto);

  void deleteCategory(Long id);
  List<CategoryDto> getAllCategories();
  List<CategoryDto> getAllParentCategories();
  List<SubCategoryDto> getSubCategoriesByParentId(Long parentId);

}
