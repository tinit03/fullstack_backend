package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import java.util.List;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Image} entities. This interface extends {@link
 * JpaRepository} to provide basic CRUD operations and additional methods for querying {@link Image}
 * records in the database.
 */
public interface ImageRepository extends JpaRepository<Image, Long> {

  /**
   * Finds a list of {@link Image} records associated with a specific item. This method retrieves
   * all images related to a particular item based on its ID.
   *
   * @param itemId the ID of the item to which the images are associated
   * @return a list of {@link Image} records related to the specified item
   */
  List<Image> findByItem_ItemId(Long itemId);
}
