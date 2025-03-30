package org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.CategoryMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.CategoryRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {
  private final CategoryRepository categoryRepository;
  @Autowired
  public CategoryServiceImpl(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Override
  public Category createCategory(CategoryDto dto) {
    Category category = CategoryMapper.toCategory(dto);
    return categoryRepository.save(category);
  }

  @Override
  public Category createSubCategory(SubCategoryDto dto) {
    Category parent = categoryRepository.findById(dto.getParentCategoryId())
        .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));

    Category subCategory = CategoryMapper.toSubCategory(dto, parent);
    return categoryRepository.save(subCategory);
  }

  @Override
  public Category updateCategory(Long id, CategoryDto dto) {
    Category existing = categoryRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    existing.setCategoryName(dto.getName());
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
}
