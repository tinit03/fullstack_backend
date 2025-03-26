package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
public class CategoryController {
  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService){
    this.categoryService = categoryService;
  }

  @PostMapping
  public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryDto dto) {
    return ResponseEntity.ok(categoryService.createCategory(dto));
  }

  @PostMapping("/sub")
  public ResponseEntity<Category> createSubCategory(@Valid @RequestBody SubCategoryDto dto) {
    return ResponseEntity.ok(categoryService.createSubCategory(dto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDto categoryDto) {
    return ResponseEntity.ok(categoryService.updateCategory(id, categoryDto));
  }

  @PutMapping("/sub/{id}")
  public ResponseEntity<Category> updateSubCategory(@PathVariable Long id, @Valid @RequestBody SubCategoryDto subCategoryDto) {
    return ResponseEntity.ok(categoryService.updateSubCategory(id, subCategoryDto));
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
