package org.ntnu.idi.idatt2105.fant.org.fantorg.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryCreateDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryDto;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;

public class CategoryMapper {

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

  //Maps only parent-category
  public static CategoryDto toParentCategoryDto(Category category) {
    CategoryDto dto = new CategoryDto();
    dto.setName(category.getCategoryName());
    dto.setId(category.getCategoryId());
    dto.setImage(ImageMapper.toDto(category.getImage()));
    return dto;
  }
  // maps only sub-categories
  public static SubCategoryDto toSubCategoryDto(Category subCategory) {
    SubCategoryDto dto = new SubCategoryDto();
    dto.setId(subCategory.getCategoryId());
    dto.setName(subCategory.getCategoryName());
    if (subCategory.getParentCategory() != null) {
      dto.setParentCategoryId(subCategory.getParentCategory().getCategoryId());
    }
    return dto;
  }

  public static Category toCategory(CategoryDto dto){
    Category category = new Category();
    category.setCategoryName(dto.getName());
    return category;
  }

  public static Category toSubCategory(SubCategoryCreateDto dto,Category parentCategory){
    Category subcategory = new Category();
    subcategory.setCategoryName(dto.getName());
    subcategory.setParentCategory(parentCategory);
    return subcategory;
  }

  public static List<CategoryDto> toParentCategoryDtoList(List<Category> categories) {
    return categories.stream()
        .filter(cat -> cat.getParentCategory() == null)
        .map(CategoryMapper::toParentCategoryDto)
        .collect(Collectors.toList());
  }
  public static List<SubCategoryDto> toSubCategoryDtoList(List<Category> categories) {
    return categories.stream()
        .filter(cat -> cat.getParentCategory()!=null)
        .map(CategoryMapper::toSubCategoryDto)
        .collect(Collectors.toList());
  }

  public static List<CategoryDto> toCategoryDtoList(List<Category> categories) {
    return categories.stream()
        .filter(cat -> cat.getParentCategory() == null)
        .map(CategoryMapper::toCategoryDto)
        .collect(Collectors.toList());
  }
}
