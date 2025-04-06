  package org.ntnu.idi.idatt2105.fant.org.fantorg.factory;

  import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryCreateDto;
  import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.CategoryDto;
  import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.category.SubCategoryDto;
  import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;

  public class TestCategoryFactory {

    /**
     * Creates a parent category with a given name and ID (optional for test referencing).
     */
    public static Category createCategory(Long id, String name) {
      Category category = new Category();
      category.setCategoryName(name);
      if (id != null) {
        setId(category, id);
      }
      return category;
    }

    /**
     * Creates a subcategory and links it to a parent category.
     */
    public static Category createSubCategory(Long id, String name, Category parent) {
      Category sub = new Category();
      sub.setCategoryName(name);
      sub.setParentCategory(parent);
      if (id != null) {
        setId(sub, id);
      }
      return sub;
    }
    public static CategoryCreateDto createCategoryDto(String name) {
      CategoryCreateDto dto = new CategoryCreateDto();
      dto.setName(name);
      return dto;
    }

    public static SubCategoryDto createSubCategoryDto(String name, Long parentId) {
      SubCategoryDto dto = new SubCategoryDto();
      dto.setName(name);
      dto.setParentCategoryId(parentId);
      return dto;
    }


    // Uses reflection to set ID if the setter is not available
    private static void setId(Category category, Long id) {
      try {
        var field = Category.class.getDeclaredField("categoryId");
        field.setAccessible(true);
        field.set(category, id);
      } catch (NoSuchFieldException | IllegalAccessException e) {
        throw new RuntimeException("Failed to set ID for Category", e);
      }
    }
  }
