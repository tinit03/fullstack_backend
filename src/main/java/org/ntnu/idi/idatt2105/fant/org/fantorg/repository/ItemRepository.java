package org.ntnu.idi.idatt2105.fant.org.fantorg.repository;

import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Category;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Item} entities. This interface extends {@link
 * JpaRepository} to provide basic CRUD operations and {@link JpaSpecificationExecutor} for more
 * advanced querying of items in the database.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

  /**
   * Finds a page of {@link Item} records by item title. This method retrieves items by their title,
   * which must match exactly the provided item name.
   *
   * @param itemName the exact title of the item to be searched
   * @param pageable the pagination information
   * @return a page of {@link Item} records that match the given title
   */
  Page<Item> findItemByTitle(String itemName, Pageable pageable);

  /**
   * Finds a page of {@link Item} records by seller. This method retrieves items listed by a
   * specific seller.
   *
   * @param seller the seller whose items are to be fetched
   * @param pageable the pagination information
   * @return a page of {@link Item} records listed by the specified seller
   */
  Page<Item> findItemBySeller(User seller, Pageable pageable);

  /**
   * Finds a page of {@link Item} records that contain the provided keyword in their title and
   * belong to the specified subcategory. This method performs a case-insensitive search for items
   * matching the keyword in their title within a specific subcategory.
   *
   * @param keyword the keyword to search for in the item title
   * @param category the subcategory of the items to be searched
   * @param pageable the pagination information
   * @return a page of {@link Item} records matching the search criteria
   */
  Page<Item> findByTitleContainingIgnoreCaseAndSubCategory(
      String keyword, Category category, Pageable pageable);

  /**
   * Finds a page of {@link Item} records that contain the provided keyword in their title. This
   * method performs a case-insensitive search for items matching the keyword in their title.
   *
   * @param keyword the keyword to search for in the item title
   * @param pageable the pagination information
   * @return a page of {@link Item} records matching the search criteria
   */
  Page<Item> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

  /**
   * Finds a page of {@link Item} records belonging to a specific subcategory. This method retrieves
   * items that belong to the specified subcategory.
   *
   * @param category the subcategory of the items to be searched
   * @param pageable the pagination information
   * @return a page of {@link Item} records belonging to the specified subcategory
   */
  Page<Item> findBySubCategory(Category category, Pageable pageable);
}
