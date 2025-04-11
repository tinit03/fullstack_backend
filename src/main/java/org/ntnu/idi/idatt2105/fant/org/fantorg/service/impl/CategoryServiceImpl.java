package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.CategoryMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.CategoryRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.ImageRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.CategoryService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.CloudinaryService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link CategoryService} for managing categories and subcategories.
 * Provides methods for creation, retrieval, updating, and deletion of categories.
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
  private final CategoryRepository categoryRepository;
  private final ImageService imageService;

  /**
   * Creates a new parent category with optional image.
   *
   * @param dto The DTO containing category name and image URL.
   * @return The saved {@link Category} entity.
   */
  @Override
  public Category createCategory(CategoryCreateDto dto) {
    Category category = new Category();
    category.setCategoryName(dto.getName());
    if(dto.getImage()!=null){
      String url = dto.getImage().getUrl();
      handleCategoryImage(category,url);
    }
    return categoryRepository.save(category);
  }

  /**
   * Creates a new subcategory linked to a parent category.
   *
   * @param dto The DTO containing the subcategory data and parent category ID.
   * @return The saved {@link Category} representing the subcategory.
   * @throws IllegalArgumentException If the parent category does not exist.
   */
  @Override
  public Category createSubCategory(SubCategoryCreateDto dto) {
    Category parent = categoryRepository.findById(dto.getParentCategoryId())
        .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
    Category subCategory = CategoryMapper.toSubCategory(dto, parent);
    return categoryRepository.save(subCategory);
  }

  /**
   * Updates an existing parent category's name and image.
   *
   * @param id  The ID of the category to update.
   * @param dto The new category data.
   * @return The updated {@link Category}.
   * @throws IllegalArgumentException If the category does not exist.
   */
  @Override
  public Category updateCategory(Long id, CategoryCreateDto dto) {
    Category existing = categoryRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    existing.setCategoryName(dto.getName());
    handleCategoryImage(existing,dto.getImage().getUrl());
    return categoryRepository.save(existing);
  }

  /**
   * Updates a subcategory's name and parent relationship.
   *
   * @param id  The ID of the subcategory to update.
   * @param dto The updated subcategory data.
   * @return The updated {@link Category} entity.
   * @throws IllegalArgumentException If subcategory or parent is not found.
   */
  @Override
  public Category updateSubCategory(Long id, SubCategoryDto dto) {
    // Finding
    Category subCategory = categoryRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Subcategory not found"));

    Category parent = categoryRepository.findById(dto.getParentCategoryId())
        .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
    subCategory.setCategoryName(dto.getName());
    subCategory.setParentCategory(parent);
    return categoryRepository.save(subCategory);
  }

  /**
   * Deletes a category or subcategory by ID.
   *
   * @param id The ID of the category to delete.
   */
  @Override
  public void deleteCategory(Long id) {
    categoryRepository.deleteById(id);
  }

  /**
   * Retrieves all categories including both parent and subcategories.
   *
   * @return A list of {@link CategoryDto}.
   */
  @Override
  public List<CategoryDto> getAllCategories(){
    return CategoryMapper.toCategoryDtoList(categoryRepository.findAll());
  }

  /**
   * Retrieves only parent categories (those without a parent).
   *
   * @return A list of {@link CategoryDto} representing parent categories.
   */
  @Override
  public List<CategoryDto> getAllParentCategories() {
    return CategoryMapper.toParentCategoryDtoList(categoryRepository.findAll());
  }

  /**
   * Retrieves all subcategories for a given parent category.
   *
   * @param parentId The ID of the parent category.
   * @return A list of {@link SubCategoryDto} under the specified parent.
   */
  @Override
  public List<SubCategoryDto> getSubCategoriesByParentId(Long parentId) {
    List<Category> subCategories = categoryRepository.findByParentCategory_CategoryId(parentId);
    return CategoryMapper.toSubCategoryDtoList(subCategories);
  }

  /**
   * Handles image assignment or update for a category. Removes image if invalid or blank.
   *
   * @param category  The category to update.
   * @param imageUrl  The image URL to assign.
   */
  private void handleCategoryImage(Category category, String imageUrl) {
    if ((imageUrl == null || imageUrl.isBlank()) || !imageUrl.startsWith("http")) {
      category.setImage(null);
      categoryRepository.save(category); // Unlink image first
      return;
    }
    Image newImage = imageService.updateImage(imageUrl, category.getImage());
    category.setImage(newImage);
  }
}
