package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Category} entities. This interface extends {@link
 * JpaRepository} to provide basic CRUD operations and additional methods for managing {@link
 * Category} records in the database.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

  /**
   * Finds a list of {@link Category} records that are children of a given parent category. This
   * method retrieves all categories that have the specified parent category.
   *
   * @param parentId the ID of the parent category
   * @return a list of {@link Category} records that are children of the specified parent category
   */
  List<Category> findByParentCategory_CategoryId(Long parentId);
}
