package org.ntnu.idi.idatt2105.fant.org.fantorg.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.image.ImageUploadDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.CategoryRepository;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.ImageService;
import org.ntnu.idi.idatt2105.fant.org.fantorg.service.impl.CategoryServiceImpl;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private ImageService imageService;

  @InjectMocks
  private CategoryServiceImpl categoryService;

  // Sample values for tests.
  private final String validImageUrl = "https://example.com/image.jpg";
  private final String invalidImageUrl = "data:image/png;base64,someBase64Data";

  private Category sampleCategory;

  @BeforeEach
  public void setUp() {
    sampleCategory = new Category();
    sampleCategory.setCategoryName("Sample Category");
    sampleCategory.setImage(null);
  }

  @Test
  public void testCreateCategory_WithValidImage() {
    CategoryCreateDto dto = new CategoryCreateDto();
    dto.setName("New Category");
    ImageUploadDto imageUploadDto = new ImageUploadDto();
    imageUploadDto.setUrl(validImageUrl);
    dto.setImage(imageUploadDto);

    // When a valid image URL is passed, the code calls imageService.updateImage
    Image newImage = new Image();
    newImage.setUrl(validImageUrl);
    newImage.setPublicId("public_id_123");
    // Simulate the image service response
    when(imageService.updateImage(eq(validImageUrl), any())).thenReturn(newImage);

    when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
      Category c = invocation.getArgument(0);
      ReflectionTestUtils.setField(c, "categoryId", 1L);
      return c;
    });

    Category created = categoryService.createCategory(dto);
    verify(imageService, times(1)).updateImage(eq(validImageUrl), any());
    assertThat(created.getCategoryName()).isEqualTo("New Category");
    assertThat(created.getImage()).isEqualTo(newImage);
    Long id = (Long) ReflectionTestUtils.getField(created, "categoryId");
    assertThat(id).isEqualTo(1L);
  }

  @Test
  public void testCreateCategory_WithInvalidImage() {
    CategoryCreateDto dto = new CategoryCreateDto();
    dto.setName("Category Without Image");
    ImageUploadDto image = new ImageUploadDto();
    image.setUrl(invalidImageUrl);
    dto.setImage(image);

    // In this branch, the service checks and sets the image to null
    // We simulate repository.save without calling the imageService
    when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
      Category c = invocation.getArgument(0);
      ReflectionTestUtils.setField(c, "categoryId", 2L);
      return c;
    });

    Category created = categoryService.createCategory(dto);

    verify(imageService, never()).updateImage(any(), any());
    assertThat(created.getCategoryName()).isEqualTo("Category Without Image");
    assertThat(created.getImage()).isNull();
    Long id = (Long) ReflectionTestUtils.getField(created, "categoryId");
    assertThat(id).isEqualTo(2L);
  }

  @Test
  public void testCreateSubCategory() {
    Category parent = new Category();
    ReflectionTestUtils.setField(parent, "categoryId", 10L);
    parent.setCategoryName("Parent Category");

    when(categoryRepository.findById(10L)).thenReturn(Optional.of(parent));
    SubCategoryCreateDto dto = new SubCategoryCreateDto();
    dto.setName("Child Category");
    dto.setParentCategoryId(10L);


    when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
      Category c = invocation.getArgument(0);
      ReflectionTestUtils.setField(c, "categoryId", 20L);
      return c;
    });

    Category createdSub = categoryService.createSubCategory(dto);

    assertThat(createdSub.getCategoryName()).isEqualTo("Child Category");
    assertThat(createdSub.getParentCategory()).isEqualTo(parent);
    Long id = (Long) ReflectionTestUtils.getField(createdSub, "categoryId");
    assertThat(id).isEqualTo(20L);
  }

  @Test
  public void testUpdateCategory() {
    Category existing = new Category();
    existing.setCategoryName("Old Category");
    ReflectionTestUtils.setField(existing, "categoryId", 30L);
    existing.setImage(null);
    when(categoryRepository.findById(30L)).thenReturn(Optional.of(existing));
    when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

    CategoryCreateDto dto = new CategoryCreateDto();
    dto.setName("Updated Category");
    ImageUploadDto imageUploadDto = new ImageUploadDto();
    imageUploadDto.setUrl(validImageUrl);
    dto.setImage(imageUploadDto);

    Image newImage = new Image();
    newImage.setUrl(validImageUrl);
    newImage.setPublicId("new_public_id");
    when(imageService.updateImage(eq(validImageUrl), any())).thenReturn(newImage);

    // When updateCategory is called.
    Category updated = categoryService.updateCategory(30L, dto);

    // Check that the name and image are updated
    assertThat(updated.getCategoryName()).isEqualTo("Updated Category");
    assertThat(updated.getImage()).isEqualTo(newImage);
    Long id = (Long) ReflectionTestUtils.getField(updated, "categoryId");
    assertThat(id).isEqualTo(30L);
  }

  @Test
  public void testUpdateSubCategory() {
    // Given an existing subcategory with a parent
    Category subCategory = new Category();
    subCategory.setCategoryName("Old Subcategory");
    ReflectionTestUtils.setField(subCategory, "categoryId", 40L);
    Category oldParent = new Category();
    ReflectionTestUtils.setField(oldParent, "categoryId", 50L);
    oldParent.setCategoryName("Old Parent");
    subCategory.setParentCategory(oldParent);

    when(categoryRepository.findById(40L)).thenReturn(Optional.of(subCategory));

    Category newParent = new Category();
    newParent.setCategoryName("New Parent");
    ReflectionTestUtils.setField(newParent, "categoryId", 60L);
    when(categoryRepository.findById(60L)).thenReturn(Optional.of(newParent));

    // Prepare SubCategoryDto for update
    SubCategoryDto updateDto = new SubCategoryDto();
    updateDto.setId(40L);
    updateDto.setName("Updated Subcategory");
    updateDto.setParentCategoryId(60L);

    when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When updateSubCategory is called
    Category updatedSub = categoryService.updateSubCategory(40L, updateDto);

    // Verify changes
    assertThat(updatedSub.getCategoryName()).isEqualTo("Updated Subcategory");
    assertThat(updatedSub.getParentCategory()).isEqualTo(newParent);
  }

  @Test
  public void testDeleteCategory() {
    // When deleteCategory is called
    categoryService.deleteCategory(70L);

    // Then, verify that deleteById is called once with id=70
    verify(categoryRepository, times(1)).deleteById(70L);
  }

  @Test
  public void testGetAllCategories() {
    // Given a list of categories
    Category c1 = new Category();
    c1.setCategoryName("Category One");
    ReflectionTestUtils.setField(c1, "categoryId", 80L);
    Category c2 = new Category();
    c2.setCategoryName("Category Two");
    ReflectionTestUtils.setField(c2, "categoryId", 81L);

    when(categoryRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

    // When getAllCategories is called
    List<CategoryDto> dtos = categoryService.getAllCategories();

    // Then, verify that the DTO list contains both categories
    assertThat(dtos).hasSize(2);
    // Assuming the mapper maps categoryName into name.
    assertThat(dtos.get(0).getName()).isEqualTo("Category One");
    assertThat(dtos.get(1).getName()).isEqualTo("Category Two");
  }

  @Test
  public void testGetSubCategoriesByParentId() {
    Category parent = new Category();
    ReflectionTestUtils.setField(parent, "categoryId", 90L);
    parent.setCategoryName("Parent Category");

    Category sub1 = new Category();
    sub1.setCategoryName("Subcategory 1");
    ReflectionTestUtils.setField(sub1, "categoryId", 91L);
    sub1.setParentCategory(parent);
    Category sub2 = new Category();
    sub2.setCategoryName("Subcategory 2");
    ReflectionTestUtils.setField(sub2, "categoryId", 92L);
    sub2.setParentCategory(parent);
    when(categoryRepository.findByParentCategory_CategoryId(90L))
        .thenReturn(Arrays.asList(sub1, sub2));

    // When getSubCategoriesByParentId is called
    List<SubCategoryDto> subDtos =
        categoryService.getSubCategoriesByParentId(90L);
    //Check if the size of the list and the names are correct
    assertThat(subDtos).hasSize(2);
    assertThat(subDtos.get(0).getName()).isEqualTo("Subcategory 1");
    assertThat(subDtos.get(1).getName()).isEqualTo("Subcategory 2");
  }
}
