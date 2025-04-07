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

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
  private final CategoryRepository categoryRepository;
  private final ImageService imageService;

  @Override
  public Category createCategory(CategoryCreateDto dto) {
    Category category = new Category();
    category.setCategoryName(dto.getName());
    String url = dto.getImage().getUrl();
    handleCategoryImage(category,url);
    return categoryRepository.save(category);
  }

  @Override
  public Category createSubCategory(SubCategoryCreateDto dto) {
    Category parent = categoryRepository.findById(dto.getParentCategoryId())
        .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
    Category subCategory = CategoryMapper.toSubCategory(dto, parent);
    return categoryRepository.save(subCategory);
  }

  @Override
  public Category updateCategory(Long id, CategoryCreateDto dto) {
    Category existing = categoryRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    existing.setCategoryName(dto.getName());
    handleCategoryImage(existing,dto.getImage().getUrl());
    return categoryRepository.save(existing);
  }

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
  @Override
  public void deleteCategory(Long id) {
    categoryRepository.deleteById(id);
  }

  @Override
  public List<CategoryDto> getAllCategories(){
    return CategoryMapper.toCategoryDtoList(categoryRepository.findAll());
  }
  @Override
  public List<CategoryDto> getAllParentCategories() {
    return CategoryMapper.toParentCategoryDtoList(categoryRepository.findAll());
  }
  @Override
  public List<SubCategoryDto> getSubCategoriesByParentId(Long parentId) {
    List<Category> subCategories = categoryRepository.findByParentCategory_CategoryId(parentId);
    return CategoryMapper.toSubCategoryDtoList(subCategories);
  }
  private void handleCategoryImage(Category category, String imageUrl) {
    if ((imageUrl == null || imageUrl.isBlank()) || !imageUrl.startsWith("http")) {
      category.setImage(null);
      categoryRepository.save(category); // Unlink image first
    }
    Image newImage = imageService.updateImage(imageUrl, category.getImage());
    category.setImage(newImage);
  }
}
