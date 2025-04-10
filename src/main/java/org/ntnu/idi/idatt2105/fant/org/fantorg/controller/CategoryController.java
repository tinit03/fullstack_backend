package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing categories and subcategories.
 * <p>
 * Provides endpoints to create, update, delete, and retrieve categories as well as subcategories.
 * Access to modification endpoints is restricted to users with the ADMIN role.
 *
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {

  private final CategoryService categoryService;

  /**
   * Constructs a CategoryController with the provided CategoryService.
   *
   * @param categoryService the service handling category operations
   */
  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  /**
   * Creates a new category.
   *
   * @param dto the category creation data
   * @return a ResponseEntity containing the created CategoryDto
   */
  @Operation(summary = "Create Category",
      description = "Creates a new category. Accessible only by ADMIN users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Category created successfully")
  })
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryCreateDto dto) {
    Category saved = categoryService.createCategory(dto);
    return ResponseEntity.ok(CategoryMapper.toCategoryDto(saved));
  }

  /**
   * Creates a new subcategory.
   *
   * @param dto the subcategory creation data
   * @return a ResponseEntity containing the created CategoryDto (subcategory)
   */
  @Operation(summary = "Create Subcategory",
      description = "Creates a new subcategory and assigns it to a parent category. Accessible only by ADMIN users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Subcategory created successfully")
  })
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/sub")
  public ResponseEntity<CategoryDto> createSubCategory(@Valid @RequestBody SubCategoryCreateDto dto) {
    Category saved = categoryService.createSubCategory(dto);
    return ResponseEntity.ok(CategoryMapper.toCategoryDto(saved));
  }

  /**
   * Updates an existing category.
   *
   * @param id  the ID of the category to update
   * @param categoryDto the updated category data
   * @return a ResponseEntity containing the updated CategoryDto
   */
  @Operation(summary = "Update Category",
      description = "Updates an existing category with new data. Accessible only by ADMIN users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Category updated successfully")
  })
  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryCreateDto categoryDto) {
    Category updated = categoryService.updateCategory(id, categoryDto);
    return ResponseEntity.ok(CategoryMapper.toCategoryDto(updated));
  }

  /**
   * Updates an existing subcategory.
   *
   * @param id  the ID of the subcategory to update
   * @param subCategoryDto the updated subcategory data
   * @return a ResponseEntity containing the updated SubCategoryDto
   */
  @Operation(summary = "Update Subcategory",
      description = "Updates an existing subcategory with new data. Accessible only by ADMIN users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Subcategory updated successfully")
  })
  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/sub/{id}")
  public ResponseEntity<SubCategoryDto> updateSubCategory(@PathVariable Long id, @Valid @RequestBody SubCategoryDto subCategoryDto) {
    Category updatedSub = categoryService.updateSubCategory(id, subCategoryDto);
    return ResponseEntity.ok(CategoryMapper.toSubCategoryDto(updatedSub));
  }

  /**
   * Deletes an existing category.
   *
   * @param id the ID of the category to delete
   * @return a ResponseEntity with no content if the deletion was successful
   */
  @Operation(summary = "Delete Category",
      description = "Deletes an existing category. Accessible only by ADMIN users.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Category deleted successfully")
  })
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Retrieves all categories.
   *
   * @param includeSubCategories if true, include subcategories for each category; otherwise, only return parent categories
   * @return a ResponseEntity containing a list of CategoryDto objects
   */
  @Operation(summary = "Get All Categories",
      description = "Retrieves all categories. Optionally, subcategories can be included based on the includeSubCategories flag.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
  })
  @GetMapping
  public ResponseEntity<List<CategoryDto>> getAllCategories(@RequestParam(defaultValue = "true") boolean includeSubCategories) {
    if (includeSubCategories) {
      return ResponseEntity.ok(categoryService.getAllCategories());
    } else {
      return ResponseEntity.ok(categoryService.getAllParentCategories());
    }
  }

  /**
   * Retrieves all subcategories for a given parent category.
   *
   * @param parentId the ID of the parent category
   * @return a ResponseEntity containing a list of SubCategoryDto objects belonging to the parent category
   */
  @Operation(summary = "Get Subcategories",
      description = "Retrieves all subcategories under the specified parent category.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Subcategories retrieved successfully")
  })
  @GetMapping("/{parentId}/subcategories")
  public ResponseEntity<List<SubCategoryDto>> getSubCategories(@PathVariable Long parentId) {
    return ResponseEntity.ok(categoryService.getSubCategoriesByParentId(parentId));
  }
}
