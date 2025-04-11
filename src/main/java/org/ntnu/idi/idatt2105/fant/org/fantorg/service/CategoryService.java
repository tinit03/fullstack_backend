package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;

/** Service interface for managing item categories and subcategories. */
public interface CategoryService {

  /**
   * Creates a new parent category.
   *
   * @param dto the DTO containing category creation data
   * @return the created {@link Category}
   */
  Category createCategory(CategoryCreateDto dto);

  /**
   * Creates a new subcategory under an existing parent category.
   *
   * @param dto the DTO containing subcategory creation data
   * @return the created {@link Category} (as a subcategory)
   */
  Category createSubCategory(SubCategoryCreateDto dto);

  /**
   * Updates an existing parent category.
   *
   * @param id the ID of the category to update
   * @param dto the DTO containing updated data
   * @return the updated {@link Category}
   */
  Category updateCategory(Long id, CategoryCreateDto dto);

  /**
   * Updates an existing subcategory.
   *
   * @param id the ID of the subcategory to update
   * @param dto the DTO containing updated data
   * @return the updated {@link Category}
   */
  Category updateSubCategory(Long id, SubCategoryDto dto);

  /**
   * Deletes a category or subcategory by ID.
   *
   * @param id the ID of the category to delete
   */
  void deleteCategory(Long id);

  /**
   * Retrieves all categories, including parent and subcategories.
   *
   * @return a list of all {@link CategoryDto}
   */
  List<CategoryDto> getAllCategories();

  /**
   * Retrieves only parent categories (top-level categories without a parent).
   *
   * @return a list of parent {@link CategoryDto}
   */
  List<CategoryDto> getAllParentCategories();

  /**
   * Retrieves all subcategories under a given parent category.
   *
   * @param parentId the ID of the parent category
   * @return a list of subcategory {@link SubCategoryDto}
   */
  List<SubCategoryDto> getSubCategoriesByParentId(Long parentId);
}
