package org.ntnu.idi.idatt2105.fant.org.fantorg.service;

import org.ntnu.idi.idatt2105.fant.org.fantorg.dto.item.*;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.Item;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Service interface for managing items listed by users. */
public interface ItemService {

  /**
   * Creates a new item for sale.
   *
   * @param dto Data for the new item.
   * @param seller The user creating the item.
   * @return The created Item entity.
   */
  Item createItem(ItemCreateDto dto, User seller);

  /**
   * Updates an existing item.
   *
   * @param id The item ID to update.
   * @param dto The updated data.
   * @param seller The seller making the update.
   * @return The updated Item entity.
   */
  Item updateItem(Long id, ItemEditDto dto, User seller);

  /**
   * Changes the status of an item (e.g., ACTIVE, SOLD, etc.).
   *
   * @param id The ID of the item.
   * @param status The new status.
   * @param seller The user attempting to change the status.
   * @return The updated Item entity.
   */
  Item changeStatus(Long id, Status status, User seller);

  /**
   * Deletes an item.
   *
   * @param id The ID of the item to delete.
   * @param seller The seller who owns the item.
   */
  void deleteItem(Long id, User seller);

  /**
   * Retrieves an item entity by ID. (Note: Should not be exposed to the client; use DTO instead.)
   *
   * @param id The item ID.
   * @return The Item entity.
   */
  Item getItemById(Long id);

  /**
   * Retrieves an item as a DTO and checks if it's bookmarked by the user.
   *
   * @param id The item ID.
   * @param user The user making the request.
   * @return The item DTO with bookmark info.
   */
  ItemDto getItemByIdBookmarked(Long id, User user);

  /**
   * Retrieves all items with optional status filter, paginated.
   *
   * @param pageable Paging information.
   * @param status Optional status filter.
   * @param user The user making the request (used for bookmarks).
   * @return A page of item DTOs.
   */
  Page<ItemDto> getAllItems(Pageable pageable, Status status, User user);

  /**
   * Searches for items with filters and pagination.
   *
   * @param itemSearchFilter The filter criteria.
   * @param pageable Paging info.
   * @param user The requesting user (for personalized results).
   * @return Filtered item search response.
   */
  ItemSearchResponse searchItems(ItemSearchFilter itemSearchFilter, Pageable pageable, User user);

  /**
   * Retrieves all items listed by a specific seller.
   *
   * @param seller The seller whose items to retrieve.
   * @param status Optional status filter.
   * @param pageable Paging info.
   * @return A page of item DTOs.
   */
  Page<ItemDto> getItemsBySeller(User seller, Status status, Pageable pageable);
}
