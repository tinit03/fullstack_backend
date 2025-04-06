package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.CategoryMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
public class CategoryController {
  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService){
    this.categoryService = categoryService;
  }

  @PostMapping
  public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryCreateDto dto) {
    Category saved = categoryService.createCategory(dto);
    return ResponseEntity.ok(CategoryMapper.toCategoryDto(saved));
  }

  @PostMapping("/sub")
  public ResponseEntity<CategoryDto> createSubCategory(@Valid @RequestBody SubCategoryCreateDto dto) {
    Category saved = categoryService.createSubCategory(dto);
    return ResponseEntity.ok(CategoryMapper.toCategoryDto(saved));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryCreateDto categoryDto) {
    Category updated = categoryService.updateCategory(id,categoryDto);
    return ResponseEntity.ok(CategoryMapper.toCategoryDto(updated));
  }

  @PutMapping("/sub/{id}")
  public ResponseEntity<SubCategoryDto> updateSubCategory(@PathVariable Long id, @Valid @RequestBody SubCategoryDto subCategoryDto) {
    Category updatedSub = categoryService.updateSubCategory(id, subCategoryDto);
    return ResponseEntity.ok(CategoryMapper.toSubCategoryDto(updatedSub));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<CategoryDto>> getAllCategories(@RequestParam(defaultValue = "true") boolean includeSubCategories) {
    if (includeSubCategories) {
      return ResponseEntity.ok(categoryService.getAllCategories());
    } else {
      return ResponseEntity.ok(categoryService.getAllParentCategories());
    }
  }

  @GetMapping("/{parentId}/subcategories")
  public ResponseEntity<List<SubCategoryDto>> getSubCategories(@PathVariable Long parentId) {
    return ResponseEntity.ok(categoryService.getSubCategoriesByParentId(parentId));
  }
}
