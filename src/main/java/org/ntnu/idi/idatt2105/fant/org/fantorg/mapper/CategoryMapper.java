package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;

/**
 * A utility class for mapping between Category and SubCategory entities and their corresponding DTOs.
 * <p>
 * The CategoryMapper class provides methods for converting between Category entities and CategoryDto objects,
 * as well as for converting SubCategory entities and SubCategoryDto objects.
 * </p>
 */
public class CategoryMapper {

  /**
   * Converts a Category entity to its corresponding CategoryDto.
   * <p>
   * This method maps the relevant fields of the Category entity, including its subcategories,
   * to a CategoryDto for data transfer.
   * </p>
   *
   * @param category The Category entity to be converted.
   * @return The CategoryDto containing category details.
   */
  public static CategoryDto toCategoryDto(Category category){
    CategoryDto dto = new CategoryDto();
    dto.setName(category.getCategoryName());
    dto.setId(category.getCategoryId());
    dto.setImage(ImageMapper.toDto(category.getImage()));

    List<SubCategoryDto> subDtos = category.getSubCategories()
        .stream()
        .map(CategoryMapper::toSubCategoryDto)
        .toList();

    dto.setSubcategories(subDtos);
    return dto;
  }

  /**
   * Converts a Category entity to a CategoryDto, including only the parent category fields.
   * <p>
   * This method maps only the parent category fields of the Category entity, without considering subcategories.
   * </p>
   *
   * @param category The Category entity to be converted.
   * @return The CategoryDto containing only parent category details.
   */
  public static CategoryDto toParentCategoryDto(Category category) {
    CategoryDto dto = new CategoryDto();
    dto.setName(category.getCategoryName());
    dto.setId(category.getCategoryId());
    dto.setImage(ImageMapper.toDto(category.getImage()));
    return dto;
  }

  /**
   * Converts a SubCategory entity to its corresponding SubCategoryDto.
   * <p>
   * This method maps the relevant fields of the SubCategory entity to a SubCategoryDto.
   * </p>
   *
   * @param subCategory The SubCategory entity to be converted.
   * @return The SubCategoryDto containing subcategory details.
   */
  public static SubCategoryDto toSubCategoryDto(Category subCategory) {
    SubCategoryDto dto = new SubCategoryDto();
    dto.setId(subCategory.getCategoryId());
    dto.setName(subCategory.getCategoryName());

    if (subCategory.getParentCategory() != null) {
      dto.setParentCategoryId(subCategory.getParentCategory().getCategoryId());
    }
    return dto;
  }

  /**
   * Converts a CategoryDto to its corresponding Category entity.
   * <p>
   * This method creates a new Category entity from the provided CategoryDto, setting the category name.
   * </p>
   *
   * @param dto The CategoryDto containing the data to create the Category entity.
   * @return The Category entity created from the provided data.
   */
  public static Category toCategory(CategoryDto dto){
    Category category = new Category();
    category.setCategoryName(dto.getName());
    return category;
  }

  /**
   * Converts a SubCategoryCreateDto to a SubCategory entity, with a reference to the parent category.
   * <p>
   * This method creates a new SubCategory entity from the provided SubCategoryCreateDto,
   * and assigns the provided parentCategory as its parent.
   * </p>
   *
   * @param dto The SubCategoryCreateDto containing data for the new SubCategory.
   * @param parentCategory The Category entity that is the parent of the subcategory.
   * @return The SubCategory entity created from the provided data.
   */
  public static Category toSubCategory(SubCategoryCreateDto dto, Category parentCategory){
    Category subcategory = new Category();
    subcategory.setCategoryName(dto.getName());
    subcategory.setParentCategory(parentCategory);
    return subcategory;
  }

  /**
   * Converts a list of Category entities to a list of CategoryDto objects,
   * including only parent categories (no subcategories).
   * <p>
   * This method filters the provided categories to include only parent categories
   * (i.e., categories without a parent category), and maps them to CategoryDto objects.
   * </p>
   *
   * @param categories The list of Category entities to be converted.
   * @return The list of CategoryDto objects containing parent category details.
   */
  public static List<CategoryDto> toParentCategoryDtoList(List<Category> categories) {
    return categories.stream()
        .filter(cat -> cat.getParentCategory() == null)
        .map(CategoryMapper::toParentCategoryDto)
        .collect(Collectors.toList());
  }

  /**
   * Converts a list of Category entities to a list of SubCategoryDto objects.
   * <p>
   * This method filters the provided categories to include only subcategories
   * (i.e., categories with a parent category), and maps them to SubCategoryDto objects.
   * </p>
   *
   * @param categories The list of Category entities to be converted.
   * @return The list of SubCategoryDto objects containing subcategory details.
   */
  public static List<SubCategoryDto> toSubCategoryDtoList(List<Category> categories) {
    return categories.stream()
        .filter(cat -> cat.getParentCategory() != null)
        .map(CategoryMapper::toSubCategoryDto)
        .collect(Collectors.toList());
  }

  /**
   * Converts a list of Category entities to a list of CategoryDto objects,
   * including only parent categories (no subcategories).
   * <p>
   * This method filters the provided categories to include only parent categories
   * (i.e., categories without a parent category), and maps them to CategoryDto objects.
   * </p>
   *
   * @param categories The list of Category entities to be converted.
   * @return The list of CategoryDto objects containing parent category details.
   */
  public static List<CategoryDto> toCategoryDtoList(List<Category> categories) {
    return categories.stream()
        .filter(cat -> cat.getParentCategory() == null)
        .map(CategoryMapper::toCategoryDto)
        .collect(Collectors.toList());
  }
}
