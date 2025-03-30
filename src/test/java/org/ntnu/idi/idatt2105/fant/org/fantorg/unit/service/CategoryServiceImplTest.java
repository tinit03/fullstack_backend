package org.ntnu.idi.idatt2105.fant.org.fantorg.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.factory.TestCategoryFactory;
import org.ntnu.idi.idatt2105.fant.org.fantorg.mapper.CategoryMapper;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.CategoryRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.CategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

  @Mock
  private CategoryRepository categoryRepository;

  @InjectMocks
  private CategoryServiceImpl categoryService;

  private CategoryDto categoryDto;
  private Category category;



  @Test
  void createCategory_ShouldSaveAndReturnCategory() {

    categoryDto = TestCategoryFactory.createCategoryDto("Electronics");
    category = TestCategoryFactory.createCategory(1L,"Electronics");
    when(categoryRepository.save(any(Category.class))).thenReturn(category);

    Category saved = categoryService.createCategory(categoryDto);

    assertNotNull(saved);
    assertEquals("Electronics", saved.getCategoryName());
    verify(categoryRepository, times(1)).save(any(Category.class));
  }

  @Test
  void updateCategory_ShouldUpdateCategoryName() {

    categoryDto = TestCategoryFactory.createCategoryDto("Electronics");
    category = TestCategoryFactory.createCategory(1L,"Electronics");
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
    when(categoryRepository.save(any(Category.class))).thenReturn(category);

    categoryDto.setName("Updated Electronics");

    Category updated = categoryService.updateCategory(1L, categoryDto);

    assertEquals("Updated Electronics", updated.getCategoryName());
    verify(categoryRepository).save(category);
  }

  @Test
  void createSubCategory_ShouldLinkToParentAndSave() {
    // Arrange
    Category parent = TestCategoryFactory.createCategory(10L, "Clothes");
    when(categoryRepository.findById(10L)).thenReturn(Optional.of(parent));

    var subCategoryDto = TestCategoryFactory.createSubCategoryDto("Shoes", 10L);
    var expectedSubCategory = TestCategoryFactory.createSubCategory(20L, "Shoes", parent);

    when(categoryRepository.save(any(Category.class))).thenReturn(expectedSubCategory);

    // Act
    Category result = categoryService.createSubCategory(subCategoryDto);

    // Assert
    assertNotNull(result);
    assertEquals("Shoes", result.getCategoryName());
    assertEquals(parent, result.getParentCategory());
    verify(categoryRepository).save(any(Category.class));
  }
  @Test
  void updateSubCategory_ShouldUpdateNameAndParent() {
    // Arrange
    Long subId = 20L;
    Long newParentId = 30L;

    Category oldParent = TestCategoryFactory.createCategory(10L, "Clothes");
    Category newParent = TestCategoryFactory.createCategory(newParentId, "Accessories");
    Category subCategory = TestCategoryFactory.createSubCategory(subId, "Shoes", oldParent);

    when(categoryRepository.findById(subId)).thenReturn(Optional.of(subCategory));
    when(categoryRepository.findById(newParentId)).thenReturn(Optional.of(newParent));
    when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArguments()[0]);

    SubCategoryDto updateDto = TestCategoryFactory.createSubCategoryDto("Boots", newParentId);

    // Act
    Category updated = categoryService.updateSubCategory(subId, updateDto);

    // Assert
    assertNotNull(updated);
    assertEquals("Boots", updated.getCategoryName());
    assertEquals(newParent, updated.getParentCategory());
    verify(categoryRepository).save(updated);
  }

  @Test
  void deleteCategory_ShouldCallRepositoryDeleteById() {
    // Arrange
    Long categoryId = 1L;
    Category category = TestCategoryFactory.createCategory(categoryId, "test");
    // Act
    categoryService.createCategory(CategoryMapper.toCategoryDto(category));
    categoryService.deleteCategory(categoryId);
    // Assert
    verify(categoryRepository, times(1)).deleteById(categoryId);
  }

  @Test
  void getAllCategories_ShouldReturnMappedDtos() {
    // Arrange
    Category parent1 = TestCategoryFactory.createCategory(1L, "Clothes");
    Category parent2 = TestCategoryFactory.createCategory(2L, "Electronics");

    when(categoryRepository.findAll()).thenReturn(List.of(parent1, parent2));

    // Act
    List<CategoryDto> result = categoryService.getAllCategories();

    // Assert
    assertEquals(2, result.size());
    assertEquals("Clothes", result.get(0).getName());
    assertEquals("Electronics", result.get(1).getName());
  }

  @Test
  void getAllParentCategories_ShouldReturnOnlyTopLevelCategories() {
    // Arrange
    Category parent1 = TestCategoryFactory.createCategory(1L, "Electronics");
    Category parent2 = TestCategoryFactory.createCategory(2L, "Books");
    Category sub1 = TestCategoryFactory.createSubCategory(3L, "Phones", parent1);

    List<Category> all = List.of(parent1, parent2, sub1);
    when(categoryRepository.findAll()).thenReturn(all);

    // Act
    List<CategoryDto> result = categoryService.getAllParentCategories();

    // Assert
    assertEquals(2, result.size());
    assertTrue(result.stream().anyMatch(c -> c.getName().equals("Electronics")));
    assertTrue(result.stream().noneMatch(c -> c.getName().equals("Phones")));
  }


  @Test
  void getSubCategoriesByParentId_ShouldReturnOnlySubcategoriesForParent() {
    // Arrange
    Category parent = TestCategoryFactory.createCategory(1L, "Clothes");
    Category sub1 = TestCategoryFactory.createSubCategory(2L, "Jackets", parent);
    Category sub2 = TestCategoryFactory.createSubCategory(3L, "Shoes", parent);

    when(categoryRepository.findByParentCategory_CategoryId(1L)).thenReturn(List.of(sub1, sub2));

    // Act
    List<SubCategoryDto> result = categoryService.getSubCategoriesByParentId(1L);

    // Assert
    assertEquals(2, result.size());
    assertTrue(result.stream().anyMatch(sc -> sc.getName().equals("Jackets")));
    assertTrue(result.stream().allMatch(sc -> sc.getParentCategoryId().equals(1L)));
  }
}